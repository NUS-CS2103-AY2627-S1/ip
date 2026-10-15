package duncan;

import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import duncan.task.Deadline;
import duncan.task.Event;
import duncan.task.Task;
import duncan.task.Todo;

/**
 * Loads the task list from a file on the hard disk when the program starts,
 * and writes it back whenever the list changes, so that tasks survive
 * between runs of the program.
 *
 * <p>Each task occupies one line of the file, with its fields separated by
 * tab characters. The first field is the task type ("T", "D" or "E"), the
 * second is the done status ("1" for done, "0" for not done), the third is
 * the description, and any further fields are that type's date details,
 * written in ISO-8601 form (yyyy-mm-dd) since that is both
 * {@link LocalDate}'s default text form and the format tasks are typed
 * in. Written out with the tabs shown as arrows, a file looks like:
 *
 * <pre>
 * T &#8594; 1 &#8594; borrow book
 * D &#8594; 0 &#8594; return book &#8594; 2019-12-02
 * E &#8594; 0 &#8594; project fair &#8594; 2019-12-01 &#8594; 2019-12-02
 * </pre>
 *
 * <p>A tab is used as the separator because {@link Parser} turns every tab
 * the user types into a space, so a task's description never contains one
 * and cannot be confused with the separator.
 *
 * <p>The file is read and written as UTF-8. Problems found while loading
 * (lines that are not a valid task, a file that is not valid UTF-8, or a
 * file that cannot be read) are collected as warnings for the caller to
 * show. When there is such a problem, the original file is first copied to
 * a backup next to it (e.g. "data/duncan.txt.bak") so that the next save
 * cannot destroy the only copy of it; if that copy cannot be made, saving
 * is turned off for the rest of the session instead.
 */
public class Storage {
    private static final String BACKUP_SUFFIX = ".bak";

    private static final String TYPE_TODO = "T";
    private static final String TYPE_DEADLINE = "D";
    private static final String TYPE_EVENT = "E";
    private static final String FLAG_DONE = "1";
    private static final String FLAG_NOT_DONE = "0";

    // How many fields a valid line of each task type has.
    private static final int FIELD_COUNT_TODO = 3;
    private static final int FIELD_COUNT_DEADLINE = 4;
    private static final int FIELD_COUNT_EVENT = 5;

    /** Reason given for a bad line; lines are reported to the user by number only. */
    private static final String MESSAGE_BAD_LINE = "not a valid task line";

    private static final String MESSAGE_SAVING_DISABLED = "saving is turned off for this session";

    /** The file the task list is read from and written to. */
    private final Path file;

    /** Where the original file is copied to if loading it ran into problems. */
    private final Path backupFile;

    /** Warnings about problems found by the last {@link #load()}, for the caller to show. */
    private final ArrayList<String> loadWarnings = new ArrayList<>();

    /** Whether saving is refused because a problem file could not be backed up. */
    private boolean isSavingDisabled = false;

    /**
     * Creates a storage that reads and writes the task list at the given path.
     *
     * @param filePath Path to the save file, relative to the directory the
     *     program is run from (e.g. "data/duncan.txt").
     */
    public Storage(String filePath) {
        this.file = Path.of(filePath);
        this.backupFile = Path.of(filePath + BACKUP_SUFFIX);
    }

    /**
     * Reads the saved task list from the hard disk. Lines that are not a
     * valid task are skipped. Any problem is recorded in
     * {@link #getLoadWarnings()}, and the original file is backed up.
     *
     * @return the saved tasks that could be read; an empty list if there is
     *         no save file yet (a normal first run, not a problem) or the
     *         file could not be read at all
     */
    public ArrayList<Task> load() {
        loadWarnings.clear();
        List<String> lines;
        try {
            lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        } catch (NoSuchFileException e) {
            // No save file yet, e.g. on the very first run: nothing to restore or protect.
            return new ArrayList<>();
        } catch (MalformedInputException e) {
            reportLoadProblem("WARNING: couldn't read " + file
                    + " because it isn't valid UTF-8 text; starting with an empty task list.");
            return new ArrayList<>();
        } catch (IOException e) {
            reportLoadProblem("WARNING: couldn't read " + file + " (" + describe(e)
                    + "); starting with an empty task list.");
            return new ArrayList<>();
        }
        return parseLines(lines);
    }

    /** Returns the warnings about problems found by the last {@link #load()}, if any. */
    public List<String> getLoadWarnings() {
        return List.copyOf(loadWarnings);
    }

    /**
     * Writes the whole task list to the hard disk, replacing whatever was
     * saved before. The containing folder (e.g. "data/") is created if it
     * does not exist yet.
     *
     * @param tasks the current task list
     * @throws DuncanException if saving is turned off, or the file cannot be
     *         written; the message says why
     */
    public void save(ArrayList<Task> tasks) throws DuncanException {
        if (isSavingDisabled) {
            throw new DuncanException(MESSAGE_SAVING_DISABLED);
        }
        List<String> lines = tasks.stream()
                .map(Task::toFileFormat)
                .toList();
        try {
            Path folder = file.getParent();
            if (folder != null) {
                Files.createDirectories(folder);
            }
            Files.write(file, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new DuncanException("couldn't write to " + file + " (" + describe(e) + ")");
        }
    }

    /**
     * Rebuilds a task from each line of the file, skipping empty lines, and
     * reports the numbers of any lines that are not a valid task.
     */
    private ArrayList<Task> parseLines(List<String> lines) {
        ArrayList<Task> tasks = new ArrayList<>();
        ArrayList<Integer> badLineNumbers = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isEmpty()) {
                continue;
            }
            try {
                tasks.add(parseTask(line));
            } catch (DuncanException e) {
                // Reported below, all together by line number, rather than one message per line.
                badLineNumbers.add(i + 1);
            }
        }
        if (!badLineNumbers.isEmpty()) {
            String numbers = badLineNumbers.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
            reportLoadProblem("WARNING: skipped bad lines in " + file + ": " + numbers);
        }
        return tasks;
    }

    /** Records a load problem, then backs up the original file before anything can overwrite it. */
    private void reportLoadProblem(String warning) {
        loadWarnings.add(warning);
        backUpOriginal();
    }

    /**
     * Copies the save file to the backup file, replacing any earlier backup.
     * If that fails, turns saving off, so the original is never overwritten
     * without a copy of it existing.
     */
    private void backUpOriginal() {
        String failure = "WARNING: couldn't back up " + file + " to " + backupFile;
        if (!Files.isRegularFile(file)) {
            disableSaving(failure + " (it isn't a regular file)");
            return;
        }
        try {
            Files.copy(file, backupFile, StandardCopyOption.REPLACE_EXISTING);
            loadWarnings.add("The original file was backed up to " + backupFile);
        } catch (IOException e) {
            disableSaving(failure + " (" + describe(e) + ")");
        }
    }

    /** Turns saving off for the rest of the session, and says so with the given reason. */
    private void disableSaving(String reason) {
        isSavingDisabled = true;
        loadWarnings.add(reason + ", so saving is turned off for this session.");
    }

    /**
     * Rebuilds a single task from the line that {@link Task#toFileFormat()}
     * wrote for it.
     *
     * @throws DuncanException if the line is not a valid task
     */
    private static Task parseTask(String line) throws DuncanException {
        // A limit of -1 keeps empty trailing fields, so a stray extra tab makes the field count wrong.
        String[] fields = line.split(Task.FIELD_SEPARATOR, -1);
        Task task = createTask(fields);
        if (parseDoneFlag(fields[1])) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Creates the not-done task described by a line's fields, after checking
     * that its type, field count, description and dates are all valid.
     */
    private static Task createTask(String[] fields) throws DuncanException {
        switch (fields[0]) {
            case TYPE_TODO:
                checkFieldCount(fields, FIELD_COUNT_TODO);
                return new Todo(parseDescription(fields[2]));
            case TYPE_DEADLINE:
                checkFieldCount(fields, FIELD_COUNT_DEADLINE);
                return new Deadline(parseDescription(fields[2]), parseSavedDate(fields[3]));
            case TYPE_EVENT:
                checkFieldCount(fields, FIELD_COUNT_EVENT);
                return createEvent(parseDescription(fields[2]), parseSavedDate(fields[3]),
                        parseSavedDate(fields[4]));
            default:
                throw new DuncanException(MESSAGE_BAD_LINE);
        }
    }

    /** Creates an event, unless it would end before it starts. */
    private static Event createEvent(String description, LocalDate from, LocalDate to) throws DuncanException {
        if (from.isAfter(to)) {
            throw new DuncanException(MESSAGE_BAD_LINE);
        }
        return new Event(description, from, to);
    }

    private static void checkFieldCount(String[] fields, int expectedCount) throws DuncanException {
        if (fields.length != expectedCount) {
            throw new DuncanException(MESSAGE_BAD_LINE);
        }
    }

    private static String parseDescription(String description) throws DuncanException {
        if (description.isBlank()) {
            throw new DuncanException(MESSAGE_BAD_LINE);
        }
        return description;
    }

    private static LocalDate parseSavedDate(String date) throws DuncanException {
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new DuncanException(MESSAGE_BAD_LINE);
        }
    }

    /** Returns whether a done flag means done: "1" for done, "0" for not done. */
    private static boolean parseDoneFlag(String flag) throws DuncanException {
        switch (flag) {
            case FLAG_DONE:
                return true;
            case FLAG_NOT_DONE:
                return false;
            default:
                throw new DuncanException(MESSAGE_BAD_LINE);
        }
    }

    /** Returns a short, readable reason for a file error, e.g. "permission denied". */
    private static String describe(IOException e) {
        if (e instanceof AccessDeniedException) {
            // Its message is only the file's path, which the warning already names.
            return "permission denied";
        }
        return e.getMessage();
    }
}
