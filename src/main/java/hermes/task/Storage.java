package hermes.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import hermes.HermesException;

/**
 * Reads tasks from the data file and writes them back to it.
 *
 * <p>This is the only class that knows the file exists, what a stored line
 * looks like, or how to turn one into a {@link Task}. Everything else works
 * with tasks in memory.
 */
public class Storage {

    /** The character used to separate fields in a stored line. */
    private static final String SEPARATOR = "|";

    /** Where each field sits in a stored line, in the order they are written. */
    private static final int TYPE_FIELD = 0;
    private static final int COMPLETION_FIELD = 1;
    private static final int DESCRIPTION_FIELD = 2;
    private static final int FIRST_DATE_FIELD = 3;
    private static final int SECOND_DATE_FIELD = 4;

    /** How a completed and an outstanding task are marked in the completion field. */
    private static final String COMPLETED_FLAG = "1";
    private static final String OUTSTANDING_FLAG = "0";

    private final File file;

    /** How many lines the most recent {@link #load()} could not understand. */
    private int skippedLines = 0;

    /** Whether the most recent {@link #load()} found the file but could not open it. */
    private boolean isFileUnreadable = false;

    /**
     * @param filePath where the tasks are stored, relative to the working directory.
     */
    public Storage(String filePath) {
        this.file = new File(filePath);
    }

    /**
     * Reads every task the file holds.
     *
     * <p>A missing file is treated as a first run rather than an error: the
     * data directory is created if needed and no tasks are returned, leaving
     * {@link #save(List)} to create the file.
     *
     * <p>A line that cannot be understood is counted and skipped rather than
     * allowed to stop the load, so one damaged line does not cost the user
     * every other task. Ask {@link #getSkippedLines()} how many were lost.
     *
     * <p>A file that exists but cannot be opened gives an empty list. Ask
     * {@link #isFileUnreadable()} whether that happened.
     *
     * @return the tasks read from the file, in the order they were stored.
     */
    public ArrayList<Task> load() {
        ArrayList<Task> tasks = new ArrayList<>();
        this.skippedLines = 0;
        this.isFileUnreadable = false;

        if (!this.file.exists()) {
            this.file.getParentFile().mkdirs();
            return tasks;
        }

        try (Scanner scanner = new Scanner(this.file)) {
            while (scanner.hasNextLine()) {
                Task task = parseStoredTask(scanner.nextLine().trim());

                if (task == null) {
                    this.skippedLines++;
                } else {
                    tasks.add(task);
                }
            }
        } catch (FileNotFoundException e) {
            // The file exists but could not be opened, usually for lack of permission.
            this.isFileUnreadable = true;
        }

        return tasks;
    }

    /**
     * Takes in the task saved in storage as a string and builds a {@link Task} from it.
     *
     * <p>A line that cannot be read is reported by returning null rather than
     * by throwing, so the caller can count it and carry on with the rest of
     * the file.
     *
     * @param storedTask task saved in storage.
     * @return {@link Task} built from the String saved in storage.
     */
    private Task parseStoredTask(String storedTask) {
        String[] taskParts = storedTask.split("\\|");
        TaskType taskType = TaskType.of(taskParts[TYPE_FIELD].trim());

        if (taskType == null || !isWellFormed(taskParts, taskType.getStoredFieldCount())) {
            return null;
        }

        assert taskParts.length == taskType.getStoredFieldCount()
                : "isWellFormed guarantees the field count the switch below indexes into";

        boolean isCompleted = COMPLETED_FLAG.equals(taskParts[COMPLETION_FIELD].trim());

        try {
            return switch (taskType) {
                case TODO -> new ToDo(isCompleted, taskParts[DESCRIPTION_FIELD].trim());
                case DEADLINE -> new Deadline(isCompleted, taskParts[DESCRIPTION_FIELD].trim(),
                        taskParts[FIRST_DATE_FIELD].trim());
                case EVENT -> new Event(isCompleted, taskParts[DESCRIPTION_FIELD].trim(),
                        taskParts[FIRST_DATE_FIELD].trim(), taskParts[SECOND_DATE_FIELD].trim());
            };
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Checks that a line read from the data file has the shape its type letter
     * promises, so the caller can index into it safely.
     *
     * <p>The field count must match exactly. Too few fields would cause an
     * {@code ArrayIndexOutOfBoundsException}; too many means a field contained
     * the '|' separator, which would silently truncate the task.
     *
     * @param parts the line split on '|'.
     * @param fieldCount how many fields this type of task is stored with.
     * @return true if the line is safe to read.
     */
    private boolean isWellFormed(String[] parts, int fieldCount) {
        if (parts.length != fieldCount) {
            return false;
        }

        // split() discards trailing empty fields, so length alone cannot tell
        // us a field is present but empty.
        for (String part : parts) {
            if (part.isBlank()) {
                return false;
            }
        }

        String flag = parts[COMPLETION_FIELD].trim();
        return flag.equals(OUTSTANDING_FLAG) || flag.equals(COMPLETED_FLAG);
    }

    /**
     * Writes every given task to the data file, replacing whatever it held
     * before.
     *
     * <p>The caller's list is the single source of truth: the file is only ever
     * a dump of it. Rewriting the file in full means no operation has to keep
     * file lines and list indices in step by hand.
     *
     * <p>A failed write is reported rather than fatal: the change is already in
     * memory, so the session can carry on and the user is told it will not
     * survive being closed.
     *
     * @param tasks the tasks to write, in the order they should be stored.
     * @throws HermesException if the data file could not be opened when it was
     *     loaded or could not be written.
     */
    public void save(List<Task> tasks) throws HermesException {
        // Leave the file unchanged if it could not be opened when loading. The list
        // in memory is missing the tasks in that file. Writing it out would erase them.
        if (this.isFileUnreadable) {
            throw new HermesException(String.format("""
                    Error: This change was not saved because Hermes could not open %s.
                    Close Hermes, fix the file's permissions and start Hermes again.
                    """, this.file));
        }

        List<String> lines = tasks.stream()
                .map(Task::getFileContent)
                .toList();
        try {
            Files.write(this.file.toPath(), lines);
        } catch (IOException e) {
            throw new HermesException(String.format("""
                    Alas, I could not write to %s.
                    Thy change holds for now, but it shall be lost
                    when I depart.
                    """, this.file));
        }
    }

    /**
     * Rejects any user-supplied field containing the '|' character.
     *
     * <p>Tasks are stored as pipe-separated fields, so a '|' inside a field
     * would make the saved line impossible to read back correctly. Refusing it
     * before a task is built means Hermes never writes a line it cannot
     * understand later.
     *
     * <p>This is static because it describes the storage format itself rather
     * than any particular file, so a caller can check text without holding a
     * Storage of its own.
     *
     * @param fields the parts of the task that will be written to file.
     * @throws HermesException if any field contains the separator.
     */
    public static void rejectSeparator(String... fields) throws HermesException {
        for (String field : fields) {
            if (field.contains(SEPARATOR)) {
                throw new HermesException(String.format(
                        "Forgive me, but a task may not contain '%s', for I use that mark to divide "
                                + "the fields of thy scroll when I save it.", SEPARATOR));
            }
        }
    }

    /**
     * Returns how many lines of the data file could not be understood on the
     * most recent load.
     *
     * @return the number of skipped lines, zero if the file was read in full.
     */
    public int getSkippedLines() {
        return this.skippedLines;
    }

    /**
     * Returns whether the data file existed but could not be opened on the most
     * recent load.
     *
     * @return true if the file could not be opened.
     */
    public boolean isFileUnreadable() {
        return this.isFileUnreadable;
    }
}
