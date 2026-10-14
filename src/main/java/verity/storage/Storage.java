package verity.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import verity.task.Deadline;
import verity.task.Event;
import verity.task.Task;
import verity.task.Todo;

/**
 * Reads and writes a task list to a fixed file on disk, so tasks persist
 * between runs of the program.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates a storage backed by the given file.
     *
     * @param filePath Relative path to the save file, e.g. "data/verity.txt".
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Writes the given tasks to disk, one per line, in save format.
     * Creates the file's containing directory first if it doesn't already exist.
     *
     * @param tasks Tasks to persist.
     * @throws IOException If the directory or file could not be written to.
     */
    public void save(List<Task> tasks) throws IOException {
        // Command.save() always passes TaskList.getTasks(), which is never null (an empty
        // list at worst) - a null here would be a bug in that internal collaboration.
        assert tasks != null : "tasks list must not be null";
        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        StringBuilder content = new StringBuilder();
        for (Task task : tasks) {
            content.append(task.toSaveFormat()).append(System.lineSeparator());
        }
        Files.writeString(filePath, content.toString());
    }

    /**
     * Loads tasks previously written by {@link #save}, in the order they appear in the file.
     * Returns an empty list if the file doesn't exist yet, e.g. on the very first run.
     * A line that isn't in the expected format is skipped (with a warning printed to the
     * console) rather than aborting the whole load, so one corrupted line doesn't cost the
     * user every other saved task.
     *
     * @return Tasks read from disk.
     * @throws IOException If the file exists but could not be read.
     */
    public ArrayList<Task> load() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return tasks;
        }
        List<String> lines = Files.readAllLines(filePath);
        for (int lineNumber = 1; lineNumber <= lines.size(); lineNumber++) {
            String line = lines.get(lineNumber - 1);
            if (line.isBlank()) {
                continue;
            }
            try {
                tasks.add(parseTask(line));
            } catch (CorruptedSaveDataException e) {
                System.out.println("Warning: skipping corrupted save-file line " + lineNumber
                        + " (" + e.getMessage() + ")");
            }
        }
        return tasks;
    }

    /**
     * Parses one save-format line (e.g. {@code "D | 0 | return book | June 6th"}) back into
     * the {@link Task} it represents.
     *
     * @param line Save-format line, as written by {@link Task#toSaveFormat()}.
     * @return Task the line represents.
     * @throws CorruptedSaveDataException If the line doesn't have enough fields, has an
     *                                     unrecognized task type, or an invalid done flag.
     */
    private Task parseTask(String line) throws CorruptedSaveDataException {
        String[] fields = splitFields(line);
        if (fields.length < 3) {
            throw new CorruptedSaveDataException("expected at least 3 fields, found " + fields.length);
        }
        String type = fields[0];
        boolean isDone = parseDoneFlag(fields[1]);
        String description = fields[2];
        if (description.isBlank()) {
            throw new CorruptedSaveDataException("task description cannot be empty");
        }

        Task task = buildTask(type, description, fields);
        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Splits a save-format line into its trimmed {@code " | "}-separated fields.
     *
     * @param line Save-format line to split.
     * @return Trimmed fields, in file order.
     */
    private String[] splitFields(String line) {
        String[] fields = line.split("\\|", -1);
        for (int i = 0; i < fields.length; i++) {
            fields[i] = fields[i].trim();
        }
        return fields;
    }

    /**
     * Parses a save-format done-flag field.
     *
     * @param doneFlag Done-flag field, expected to be exactly {@code "0"} or {@code "1"}.
     * @return {@code true} if the flag is {@code "1"}.
     * @throws CorruptedSaveDataException If the flag is neither {@code "0"} nor {@code "1"}.
     */
    private boolean parseDoneFlag(String doneFlag) throws CorruptedSaveDataException {
        if (!doneFlag.equals("0") && !doneFlag.equals("1")) {
            throw new CorruptedSaveDataException("done flag must be '0' or '1', found '" + doneFlag + "'");
        }
        return doneFlag.equals("1");
    }

    /**
     * Builds the task-type-specific part of a save-format line: a {@link Todo} needs no
     * further fields, a {@link Deadline} needs a due-date field, and an {@link Event} needs
     * start/end-date fields.
     *
     * @param type Task-type field ({@code "T"}, {@code "D"}, or {@code "E"}).
     * @param description Description field, already extracted by the caller.
     * @param fields All fields of the line, for type-specific ones beyond the description.
     * @return Task built from the type-specific fields, not-done by default.
     * @throws CorruptedSaveDataException If the type is unrecognized, or a type-specific
     *                                     field is missing or invalid.
     */
    private Task buildTask(String type, String description, String[] fields) throws CorruptedSaveDataException {
        switch (type) {
            case "T":
                return new Todo(description);
            case "D":
                if (fields.length < 4) {
                    throw new CorruptedSaveDataException("deadline is missing its due-date field");
                }
                return new Deadline(description, parseSavedDate(fields[3]));
            case "E":
                if (fields.length < 5) {
                    throw new CorruptedSaveDataException("event is missing its start/end-time field(s)");
                }
                LocalDate startDate = parseSavedDate(fields[3]);
                LocalDate endDate = parseSavedDate(fields[4]);
                if (endDate.isBefore(startDate)) {
                    throw new CorruptedSaveDataException("event ends before it starts");
                }
                return new Event(description, startDate, endDate);
            default:
                throw new CorruptedSaveDataException("unrecognized task type '" + type + "'");
        }
    }

    /**
     * Parses a save-file date field (ISO format, e.g. {@code "2019-10-15"}), as written by
     * {@link Deadline#toSaveFormat()}/{@link Event#toSaveFormat()}.
     *
     * @param text Date field to parse.
     * @return Parsed date.
     * @throws CorruptedSaveDataException If the text isn't a valid ISO date.
     */
    private LocalDate parseSavedDate(String text) throws CorruptedSaveDataException {
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            throw new CorruptedSaveDataException("invalid date '" + text + "', expected yyyy-MM-dd");
        }
    }
}
