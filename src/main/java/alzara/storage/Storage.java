package alzara.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

import alzara.AlzaraException;
import alzara.task.Deadline;
import alzara.task.Event;
import alzara.task.Task;
import alzara.task.ToDo;
import alzara.ui.Ui;

/**
 * Loads tasks from, and saves tasks to, a save file named {@code alzara.txt}
 * in a data directory - {@code data} (relative to the working directory the
 * program is run from) by default, or any directory a caller supplies, e.g.
 * a JUnit {@code @TempDir} in a test.
 */
public class Storage {
    private static final String DEFAULT_DATA_DIR = "data";
    private static final String FILE_NAME = "alzara.txt";
    private static final String FIELD_SEPARATOR = " \\| ";

    private static final int TYPE_INDEX = 0;
    private static final int DONE_FLAG_INDEX = 1;
    private static final int DESCRIPTION_INDEX = 2;
    private static final int DATE_INDEX = 3;
    private static final int EVENT_END_DATE_INDEX = 4;
    private static final int MIN_FIELD_COUNT = DESCRIPTION_INDEX + 1;
    private static final int MIN_FIELD_COUNT_WITH_DATE = DATE_INDEX + 1;
    private static final int MIN_FIELD_COUNT_WITH_EVENT_DATES = EVENT_END_DATE_INDEX + 1;

    private final File filePath;
    private final File tempFilePath;

    /**
     * Creates a {@link Storage} using the default {@code data} directory,
     * relative to the working directory the program is run from.
     */
    public Storage() {
        this(new File(DEFAULT_DATA_DIR));
    }

    /**
     * Creates a {@link Storage} whose save file is {@code alzara.txt} inside
     * {@code dataDir}, e.g. a JUnit {@code @TempDir} in a test, so tests
     * never touch a real save file.
     *
     * @param dataDir the directory the save file (and its temp file used by
     *         {@link #save}) live in; created on first {@link #save} if it
     *         doesn't exist yet
     */
    public Storage(File dataDir) {
        this.filePath = new File(dataDir, FILE_NAME);
        this.tempFilePath = new File(dataDir, FILE_NAME + ".tmp");
    }

    /**
     * Overwrites the save file with every task in {@code memory}, one per line
     * in each task's {@link Task#toSaveFormat()}.
     *
     * <p>Creates the {@code data} folder first if it doesn't exist yet. Prints
     * a message and returns without throwing if the folder can't be created
     * or the file can't be written - a failed save shouldn't crash the program.
     *
     * <p>Writes to a temporary file first, then atomically moves it over the
     * real save file, rather than writing directly into {@code alzara.txt}.
     * This way, if the program is interrupted mid-write (a crash, a forced
     * quit), the save file on disk is always either the old complete version
     * or the new complete version - never a truncated, half-written one.
     *
     * @param memory the current task list to persist; does nothing if {@code null}
     * @param ui where a failure to save is reported, e.g. via a chat bubble in the GUI
     */
    public void save(ArrayList<Task> memory, Ui ui) {
        if (memory == null) {
            return;
        }

        File parentDir = filePath.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            boolean wasCreated = parentDir.mkdirs();
            if (!wasCreated && !parentDir.exists()) {
                ui.showStorageMessage("The great Alzara has nowhere to keep your tasks. Nothing was saved.");
                return;
            }
        }

        try {
            FileWriter writer = new FileWriter(tempFilePath);
            for (Task task : memory) {
                writer.write(task.toSaveFormat() + System.lineSeparator());
            }
            writer.close();
            Files.move(tempFilePath.toPath(), filePath.toPath(),
                    StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException exception) {
            ui.showStorageMessage("The great Alzara's hand was stayed. Your tasks were not saved.");
            tempFilePath.delete();
        }
    }

    /**
     * Reads every task from the save file, skipping (and reporting, with its
     * line number and reason) any line that can't be parsed rather than
     * failing the whole load.
     *
     * @param ui where a read failure or corrupted-entry report is shown,
     *         e.g. via a chat bubble in the GUI
     * @return the loaded tasks, or an empty list if the save file doesn't
     *         exist yet, can't be read, or couldn't be loaded at all
     */
    public ArrayList<Task> load(Ui ui) {
        ArrayList<Task> memory = new ArrayList<>();
        if (!filePath.exists()) {
            return memory;
        }
        if (!filePath.canRead()) {
            ui.showStorageMessage("I cannot reach the old records. Let us begin anew.");
            return memory;
        }

        try {
            String corruptionReport = loadTasksFromFile(memory);
            if (!corruptionReport.isEmpty()) {
                ui.showStorageMessage(corruptionReport);
            }
        } catch (IOException exception) {
            ui.showStorageMessage("Something clouds the old records. We will begin anew.");
            return new ArrayList<>();
        }
        return memory;
    }

    /**
     * Reads every line of the save file into {@code memory}, skipping any
     * line that can't be parsed and noting it in the returned report instead
     * of failing the whole read.
     *
     * @param memory the list each successfully parsed task is added to
     * @return a report of every skipped line, one per line, or an empty
     *         string if every line loaded cleanly
     * @throws IOException if the save file can't be read
     */
    private String loadTasksFromFile(ArrayList<Task> memory) throws IOException {
        StringBuilder corruptionReport = new StringBuilder();
        Scanner scanner = new Scanner(filePath);
        int lineNumber = 0;
        while (scanner.hasNextLine()) {
            lineNumber++;
            String line = scanner.nextLine();
            if (line.trim().isEmpty()) {
                continue;
            }
            try {
                memory.add(loadTask(line));
            } catch (AlzaraException exception) {
                appendCorruptionLine(corruptionReport, lineNumber, exception.getMessage());
            }
        }
        scanner.close();
        return corruptionReport.toString();
    }

    /**
     * Appends one skipped-line entry to {@code corruptionReport}, on its own
     * line after any entry already there.
     *
     * @param corruptionReport the report being built up, one entry per line
     * @param lineNumber the 1-based save-file line number that was skipped
     * @param reason why the line couldn't be parsed
     */
    private static void appendCorruptionLine(StringBuilder corruptionReport, int lineNumber, String reason) {
        if (corruptionReport.length() > 0) {
            corruptionReport.append('\n');
        }
        corruptionReport.append("A flawed memory on line ").append(lineNumber)
                .append(" was discarded: ").append(reason);
    }

    /**
     * Parses one save-file line (e.g. {@code "T | N | read book"}) into the
     * matching {@link Task} subtype, and applies its done flag.
     *
     * @throws AlzaraException if the line has too few fields, an invalid done
     *         flag, an empty description, an unrecognised task type, a
     *         missing/malformed date field for that type, or (for an event)
     *         a start date after the end date
     */
    private static Task loadTask(String line) throws AlzaraException {
        String[] parts = line.split(FIELD_SEPARATOR);
        if (parts.length < MIN_FIELD_COUNT) {
            throw new AlzaraException("not enough fields");
        }

        String type = parts[TYPE_INDEX].trim();
        String doneFlag = parts[DONE_FLAG_INDEX].trim();
        String description = parts[DESCRIPTION_INDEX];

        if (!doneFlag.equals("N") && !doneFlag.equals("Y")) {
            throw new AlzaraException("invalid done flag");
        }
        if (description.trim().isEmpty()) {
            throw new AlzaraException("missing description");
        }

        Task task;
        switch (type) {
            case "T":
                task = new ToDo(description);
                break;
            case "D":
                task = loadDeadline(description, parts);
                break;
            case "E":
                task = loadEvent(description, parts);
                break;
            default:
                throw new AlzaraException("unrecognised task type '" + type + "'");
        }

        if (doneFlag.equals("Y")) {
            task.mark();
        }
        return task;
    }

    /**
     * Parses a save-file {@code "D"} line's remaining fields into a
     * {@link Deadline}.
     *
     * @param description the already-extracted, already-validated description
     * @param parts the save-file line's fields, split on {@link #FIELD_SEPARATOR}
     * @throws AlzaraException if the due-date field is missing or malformed
     */
    private static Task loadDeadline(String description, String[] parts) throws AlzaraException {
        if (parts.length < MIN_FIELD_COUNT_WITH_DATE || parts[DATE_INDEX].trim().isEmpty()) {
            throw new AlzaraException("missing deadline field");
        }
        try {
            return new Deadline(description, LocalDate.parse(parts[DATE_INDEX].trim()));
        } catch (DateTimeParseException exception) {
            throw new AlzaraException("invalid deadline date");
        }
    }

    /**
     * Parses a save-file {@code "E"} line's remaining fields into an
     * {@link Event}.
     *
     * @param description the already-extracted, already-validated description
     * @param parts the save-file line's fields, split on {@link #FIELD_SEPARATOR}
     * @throws AlzaraException if either date field is missing or malformed,
     *         or the start date is after the end date
     */
    private static Task loadEvent(String description, String[] parts) throws AlzaraException {
        if (parts.length < MIN_FIELD_COUNT_WITH_EVENT_DATES
                || parts[DATE_INDEX].trim().isEmpty() || parts[EVENT_END_DATE_INDEX].trim().isEmpty()) {
            throw new AlzaraException("missing event start/end field");
        }
        LocalDate eventStart;
        LocalDate eventEnd;
        try {
            eventStart = LocalDate.parse(parts[DATE_INDEX].trim());
            eventEnd = LocalDate.parse(parts[EVENT_END_DATE_INDEX].trim());
        } catch (DateTimeParseException exception) {
            throw new AlzaraException("invalid event date");
        }
        if (eventStart.isAfter(eventEnd)) {
            throw new AlzaraException("event start date after end date");
        }
        return new Event(description, eventStart, eventEnd);
    }
}
