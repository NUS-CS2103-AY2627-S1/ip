package harvey.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import harvey.HarveyException;
import harvey.task.Deadline;
import harvey.task.Event;
import harvey.task.Task;
import harvey.task.Todo;
/**
 * Writes the task list to a file on disk, so tasks survive between runs.
 * <p>
 * Keeping all file handling in its own class means {@link Harvey} does not have to know
 * where the tasks are stored or in what format. If the format changes later, only this
 * class and the {@code toFileFormat} methods change.
 */
public class Storage {
    /**
     * The same separator written as a regular expression, for splitting a line back up.
     * {@code split} treats its argument as a regex, in which {@code |} means "or", so the
     * bar has to be escaped as {@code \|} to stand for a literal bar character.
     */
    private static final String SEPARATOR_REGEX = " \\| ";

    /** Position of the type letter within a saved line. */
    private static final int FIELD_TYPE = 0;

    /** Position of the done flag within a saved line. */
    private static final int FIELD_DONE = 1;

    /** Position of the description within a saved line. */
    private static final int FIELD_DESCRIPTION = 2;

    /** Fields every saved line has, whatever task it holds: type, done flag and description. */
    private static final int SHARED_FIELD_COUNT = 3;

    /** Fields a saved todo has: the shared three and nothing more. */
    private static final int TODO_FIELD_COUNT = SHARED_FIELD_COUNT;

    /** Fields a saved deadline has: the shared three plus the due date. */
    private static final int DEADLINE_FIELD_COUNT = SHARED_FIELD_COUNT + 1;

    /** Fields a saved event has: the shared three plus a start and an end. */
    private static final int EVENT_FIELD_COUNT = SHARED_FIELD_COUNT + 2;

    /** Done flag written for a task the user has completed. */
    private static final String DONE_FLAG_TRUE = "1";

    /** Done flag written for a task the user has not completed. */
    private static final String DONE_FLAG_FALSE = "0";

    /**
     * The character Java puts in place of bytes that are not valid text. A line holding it
     * was damaged on disk, so it is treated like any other line that cannot be read.
     */
    private static final char REPLACEMENT_CHARACTER = '\uFFFD';

    /**
     * Timestamp added to a backup's name, e.g. {@code harvey.txt.20260913-162455.bak}.
     * Each backup gets its own name, so a second damaged load never overwrites the copy
     * made by the first.
     */
    private static final DateTimeFormatter BACKUP_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    /** Where the tasks are stored, relative to the folder the program is started from. */
    private final Path filePath;

    /** How many lines the most recent {@link #load()} could not understand. */
    private int skippedLines = 0;

    /** Where the file was copied before any of it could be lost, or null if it was not. */
    private Path backupPath = null;

    /**
     * Whether {@link #save(ArrayList)} may overwrite the file.
     * <p>
     * False only when the last load did not recover everything and no backup could be
     * made. Saving then would permanently destroy whatever could not be read, so it is
     * refused until the user has dealt with the file.
     */
    private boolean isOverwriteSafe = true;

    /**
     * Creates a storage that reads and writes the given file.
     * <p>
     * The path is built with {@link Paths#get(String, String...)} from separate folder and
     * file names rather than written as one string like {@code "data/harvey.txt"}. Java then
     * joins them using whatever separator the current operating system uses ({@code /} on
     * macOS and Linux, {@code \} on Windows), so the same code works everywhere. It is also
     * a relative path, so it is resolved against the project folder rather than pointing at
     * one particular computer's hard disk.
     *
     * @param folderName the folder holding the file, e.g. {@code data}.
     * @param fileName   the name of the file, e.g. {@code harvey.txt}.
     */
    public Storage(String folderName, String fileName) {
        this.filePath = Paths.get(folderName, fileName);
    }

    /**
     * Overwrites the file with the given tasks, one task per line.
     * <p>
     * The whole list is rewritten each time rather than the changed line being edited in
     * place. That is slightly wasteful, but the list is small and it removes any chance of
     * the file drifting out of step with the list held in memory.
     *
     * @param tasks the tasks to store.
     * @throws HarveyException if the file cannot be written, or holds tasks that could not
     *                         be loaded and could not be backed up.
     */
    public void save(ArrayList<Task> tasks) throws HarveyException {
        assert tasks != null : "TaskList.asList() hands out its own list, which is never null";
        if (!isOverwriteSafe) {
            throw new HarveyException("I won't save, because that would overwrite " + filePath
                    + ", which I could not fully read or back up. Move that file somewhere safe, "
                    + "then restart me.");
        }

        // Each subclass supplies its own line format, so this never needs to ask whether
        // it is holding a Todo, a Deadline or an Event. Task::toFileFormat is a method
        // reference: shorthand for the lambda task -> task.toFileFormat().
        List<String> lines = tasks.stream()
                .map(Task::toFileFormat)
                .toList();

        try {
            // The data folder does not exist in a fresh copy of the project, and writing a
            // file into a missing folder fails. createDirectories does nothing if the
            // folder is already there, so it is safe to call every time.
            Path parentFolder = filePath.getParent();
            if (parentFolder != null) {
                Files.createDirectories(parentFolder);
            }
            Files.write(filePath, lines);
        } catch (IOException e) {
            // IOException is Java's way of reporting that the disk operation failed, e.g.
            // the file is read-only. Translating it into HarveyException means Harvey
            // reports it through the same channel as every other problem.
            throw new HarveyException("I could not save your tasks to " + filePath
                    + ", because " + describe(e) + ".");
        }
    }

    /**
     * Reads back the tasks previously written by {@link #save(ArrayList)}.
     *
     * <p>
     * If the file exists but cannot be read in full, it is copied to a backup first (see
     * {@link #getBackupPath()}), because the next save rewrites the file from what was
     * loaded and would otherwise erase whatever was left out.
     *
     * @return the stored tasks, in the order they were written.
     * @throws HarveyException if the file cannot be read.
     */
    public ArrayList<Task> load() throws HarveyException {
        ArrayList<Task> tasks = new ArrayList<>();
        skippedLines = 0;
        backupPath = null;
        isOverwriteSafe = true;

        // The file is absent the first time anyone runs Harvey, which is normal rather
        // than a failure, so an empty list is returned instead of an error being raised.
        if (!Files.exists(filePath)) {
            return tasks;
        }

        // A folder with the save file's name holds no tasks to lose, so there is nothing to
        // back up; saving will fail on its own until the folder is moved.
        if (Files.isDirectory(filePath)) {
            throw new HarveyException(filePath + " is a folder, so I cannot keep your tasks there. "
                    + "Rename or move that folder, then restart me.");
        }

        try {
            // Deliberately a loop rather than a stream. toTask throws a checked exception,
            // which a lambda cannot pass on, and each damaged line has to increment a
            // counter outside the loop. Both fight the way streams are meant to be used.
            for (String line : readLinesLeniently()) {
                // Blank lines carry no task and are not a sign of damage, e.g. a trailing
                // newline at the end of the file, so they are passed over quietly.
                if (line.trim().isEmpty()) {
                    continue;
                }

                try {
                    tasks.add(toTask(line));
                } catch (HarveyException e) {
                    // One damaged line should not cost the user the rest of the file, so
                    // it is set aside and the remaining lines are still read.
                    skippedLines++;
                }
            }
        } catch (IOException e) {
            backUp();
            throw new HarveyException("I could not read your saved tasks from " + filePath
                    + ", because " + describe(e) + ".");
        }

        if (skippedLines > 0) {
            backUp();
        }
        return tasks;
    }

    /**
     * Reads the file as lines of text, tolerating bytes that are not valid text.
     * <p>
     * {@code Files.readAllLines} gives up on the whole file at the first invalid byte, e.g.
     * after the file was saved in another encoding. Decoding it this way instead replaces
     * each invalid byte with {@link #REPLACEMENT_CHARACTER}, so only the lines that hold one
     * are lost, and those are caught by {@link #toTask(String)} like any other damage.
     *
     * @return the lines of the file.
     * @throws IOException if the file cannot be read at all.
     */
    private List<String> readLinesLeniently() throws IOException {
        return new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8).lines().toList();
    }

    /**
     * Copies the save file to a new backup next to it, before its contents can be lost.
     * <p>
     * If the copy fails, saving is switched off instead (see {@link #isOverwriteSafe}), so
     * the original is never overwritten without a copy existing somewhere.
     */
    private void backUp() {
        Path backup = filePath.resolveSibling(filePath.getFileName() + "."
                + LocalDateTime.now().format(BACKUP_TIMESTAMP) + ".bak");
        try {
            Files.copy(filePath, backup, StandardCopyOption.REPLACE_EXISTING);
            backupPath = backup;
        } catch (IOException e) {
            isOverwriteSafe = false;
        }
    }

    /**
     * Returns where the last {@link #load()} backed up a file it could not fully read.
     *
     * @return the backup's path, or null if no backup was needed or none could be made.
     */
    public Path getBackupPath() {
        return backupPath;
    }

    /**
     * Returns whether the last {@link #load()} left the file safe to overwrite.
     *
     * @return false if some of the file could not be read and no backup could be made.
     */
    public boolean isOverwriteSafe() {
        return isOverwriteSafe;
    }

    /**
     * Explains in plain words why a file operation failed, for the end of an error message.
     * <p>
     * Java reports most file problems as a subclass of {@link IOException}, and the
     * subclass says what kind of problem it was. Naming it tells the user what to fix,
     * where "could not save" alone does not.
     *
     * @param e the exception the file operation threw.
     * @return a clause such as {@code permission was denied}.
     */
    static String describe(IOException e) {
        if (e instanceof AccessDeniedException) {
            return "permission was denied";
        }
        if (e instanceof FileAlreadyExistsException) {
            // Thrown by createDirectories when a file sits where the folder should be.
            return "a file is in the way where the folder should be";
        }
        if (e instanceof NoSuchFileException) {
            return "the file or its folder does not exist";
        }
        if (e instanceof FileSystemException systemException && systemException.getReason() != null) {
            // e.g. "Is a directory", when a folder has the save file's name.
            return "the system said: " + systemException.getReason();
        }
        return "the system said: " + e.getMessage();
    }

    /**
     * Returns how many lines the last {@link #load()} could not understand.
     * <p>
     * The count is kept in a field so that {@code load} can still return the tasks it did
     * understand. A tidier design would return one object holding both the tasks and the
     * count, but that is more machinery than a single number needs here.
     *
     * @return the number of damaged lines skipped, or {@code 0} if the file was intact.
     */
    public int getSkippedLines() {
        return skippedLines;
    }

    /**
     * Rebuilds one task from one line of the save file, reversing {@code toFileFormat}.
     * <p>
     * The file is a plain text file that anyone can open and edit, so a line cannot be
     * assumed to be well formed. Every part is checked before any task is built, and
     * anything unexpected is reported as a {@link HarveyException} for the caller to deal
     * with, rather than being allowed to reach the constructors.
     *
     * @param line a line such as {@code D | 0 | return book | Sunday}.
     * @return the task that line describes.
     * @throws HarveyException if the line is not in the expected format.
     */
    private static Task toTask(String line) throws HarveyException {
        // ["D", "0", "return book", "Sunday"] for the example above.
        String[] fields = line.split(SEPARATOR_REGEX);
        requireSharedFields(fields, line);

        String doneFlag = fields[FIELD_DONE];
        Task task = buildTask(fields, line);
        assert task != null : "Every branch of buildTask either returns a task or throws";

        if (doneFlag.equals(DONE_FLAG_TRUE)) {
            // Every task is built as not-done, so the stored flag is applied afterwards
            // rather than being passed through four separate constructors.
            task.markAsDone();
        }
        return task;
    }

    /**
     * Checks the parts every saved line must have, whatever kind of task it holds.
     *
     * @param fields the fields the line was split into.
     * @param line   the original line, for use in the error message.
     * @throws HarveyException if a shared field is missing, empty or not one of the values written.
     */
    private static void requireSharedFields(String[] fields, String line) throws HarveyException {
        if (fields.length < SHARED_FIELD_COUNT) {
            throw new HarveyException("Line has too few fields: " + line);
        }
        if (fields[FIELD_DESCRIPTION].isEmpty()) {
            throw new HarveyException("Task has no description: " + line);
        }

        // Only the two flags above are ever written, so anything else means the line was
        // edited by hand and its done state cannot be trusted.
        String doneFlag = fields[FIELD_DONE];
        if (!doneFlag.equals(DONE_FLAG_TRUE) && !doneFlag.equals(DONE_FLAG_FALSE)) {
            throw new HarveyException("Done flag is neither " + DONE_FLAG_TRUE + " nor "
                    + DONE_FLAG_FALSE + ": " + line);
        }
    }

    /**
     * Builds the kind of task the type letter names, from fields already known to be sound.
     * <p>
     * Note that loading cannot be polymorphic the way saving is. When saving, each task
     * already exists and can be asked for its own line. When loading there is no task yet
     * to ask, so something has to read the type letter and decide which subclass to build;
     * that decision lives here.
     *
     * @param fields the fields the line was split into.
     * @param line   the original line, for use in the error message.
     * @return the task those fields describe, not yet marked as done.
     * @throws HarveyException if the type letter is unknown or the line has the wrong number of fields.
     */
    private static Task buildTask(String[] fields, String line) throws HarveyException {
        String description = fields[FIELD_DESCRIPTION];

        // How many fields the line should have depends on its type, so the expected count
        // is checked before any of the extra fields are read. Without this, a truncated
        // deadline line would fail with an array error instead of a clear message.
        switch (fields[FIELD_TYPE]) {
            case "T":
                requireFieldCount(fields, TODO_FIELD_COUNT, line);
                return new Todo(description);
            case "D":
                requireFieldCount(fields, DEADLINE_FIELD_COUNT, line);
                // Reusing parseDate means a hand-edited date in the file is caught the same
                // way as a mistyped one, and is skipped as a damaged line.
                return new Deadline(description, Deadline.parseDate(fields[SHARED_FIELD_COUNT]));
            case "E":
                requireFieldCount(fields, EVENT_FIELD_COUNT, line);
                // Reusing parseDateTime does for an event what parseDate does for a
                // deadline: a hand-edited time is caught here and the line is skipped.
                return new Event(description, Event.parseDateTime(fields[SHARED_FIELD_COUNT]),
                        Event.parseDateTime(fields[SHARED_FIELD_COUNT + 1]));
            default:
                // Previously an unknown letter quietly became a Todo, which turned damaged
                // data into a wrong task. Rejecting it is safer than guessing.
                throw new HarveyException("Unknown task type \"" + fields[FIELD_TYPE]
                        + "\": " + line);
        }
    }

    /**
     * Checks that a line was split into exactly the number of fields its type needs.
     *
     * @param fields   the fields the line was split into.
     * @param expected how many fields this type of task should have.
     * @param line     the original line, for use in the error message.
     * @throws HarveyException if the count does not match.
     */
    private static void requireFieldCount(String[] fields, int expected, String line)
            throws HarveyException {
        if (fields.length != expected) {
            throw new HarveyException("Expected " + expected + " fields but found "
                    + fields.length + ": " + line);
        }
    }
}
