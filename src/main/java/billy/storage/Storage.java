package billy.storage;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import billy.BillyException;
import billy.task.Deadline;
import billy.task.Event;
import billy.task.Task;
import billy.task.TaskDate;
import billy.task.Todo;

/**
 * Keeps the task list on the hard disk, so it survives Billy being closed.
 *
 * <p>The file is a plain text one, holding one task per line with its fields
 * separated by {@value Task#FIELD_SEPARATOR}:
 *
 * <pre>
 * T | 1 | read book
 * D | 0 | return book | 2019-12-02 1800
 * E | 0 | project meeting | 2019-12-02 1400 | 2019-12-02 1600
 * </pre>
 *
 * <p>The first field is the type letter, the second is 1 for a finished task and
 * 0 for an unfinished one, and the rest are the task's own details. A readable
 * text format was chosen over Java's object serialization because a person can
 * open it, understand it and fix it by hand — useful while the program is still
 * being built.
 *
 * <p>Nothing here assumes the file, or the folder holding it, already exists:
 * both are created when first needed, and a missing file simply means there is
 * nothing to load yet.
 */
public class Storage {

    /**
     * Matches {@value Task#FIELD_SEPARATOR} when reading a line back.
     *
     * <p>Spaces around the bar are optional here so that a file tidied up by hand
     * still loads.
     */
    private static final String FIELD_PATTERN = "\\s*\\|\\s*";

    /** The file the tasks are read from and written to. */
    private final Path file;

    /**
     * How many lines the last {@link #load()} could not make sense of.
     *
     * <p>Recorded rather than printed, so that the wording of the warning stays
     * with the rest of Billy's messages instead of being spread across classes.
     */
    private int skippedLineCount = 0;

    /**
     * Creates storage backed by a particular file.
     *
     * @param file where the tasks are kept; a relative path is resolved against
     *             the folder Billy is run from
     */
    public Storage(Path file) {
        this.file = file;
    }

    /**
     * Returns the file the tasks are kept in, for use in messages to the user.
     *
     * @return the file this storage reads from and writes to
     */
    public Path getPath() {
        return file;
    }

    /**
     * Returns how many lines the last {@link #load()} skipped as unreadable.
     *
     * @return the number of damaged lines seen by the last load, or 0
     */
    public int getSkippedLineCount() {
        return skippedLineCount;
    }

    /**
     * Reads the saved tasks back from the file.
     *
     * <p>A missing file is not a failure: it means nothing has been saved yet,
     * which is exactly what happens the first time Billy is run on a computer.
     *
     * <p>A line that cannot be understood is skipped rather than abandoning the
     * whole file, so one damaged line never costs the user the rest of their
     * list. {@link #getSkippedLineCount()} reports whether that happened.
     *
     * @return the tasks that were read, in the order they were saved
     * @throws BillyException if the file is there but cannot be read at all
     */
    public ArrayList<Task> load() throws BillyException {
        skippedLineCount = 0;
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.isRegularFile(file)) {
            return tasks;
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(file);
        } catch (IOException e) {
            throw new BillyException("I couldn't read " + file + " (" + describeFailure(e)
                    + "), so I'm starting with an empty list.");
        }

        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }
            try {
                tasks.add(parseTask(line));
            } catch (BillyException e) {
                // The line is damaged. Count it and carry on with the rest.
                skippedLineCount++;
            }
        }
        return tasks;
    }

    /**
     * Writes the whole list out, replacing whatever the file held before.
     *
     * <p>Rewriting the file each time is simpler than working out which line
     * changed, and with the handful of tasks a person keeps it costs nothing
     * noticeable.
     *
     * @param tasks the tasks to store, in the order they should be read back
     * @throws IOException if the folder or the file cannot be written
     */
    public void save(List<Task> tasks) throws IOException {
        Path folder = file.getParent();
        if (folder != null) {
            // Does nothing if the folder is already there, so no need to check first.
            Files.createDirectories(folder);
        }

        List<String> lines = tasks.stream()
                .map(Task::toSaveFormat)
                .toList();
        // Creates the file if it is missing, and empties it if it is not.
        Files.write(file, lines);
    }

    /**
     * Says what went wrong with a file, without repeating the file's name.
     *
     * <p>Java's file exceptions carry the path as their message and add a reason
     * only when they know one, so {@code getMessage()} on its own often just
     * echoes back the path Billy has already named. Asking for the reason, and
     * naming the kind of failure when there is none, keeps the warning useful.
     *
     * <p>Public because the commands that save the list word their own warning
     * around it, and they now live in a package of their own. Being visible to
     * the whole program is the price of that; it is safe to pay here, since this
     * only turns an exception into words and holds nothing back.
     *
     * @param e the failure to describe
     * @return a short phrase to put in brackets after the file's name
     */
    public static String describeFailure(IOException e) {
        // Checked first: the more specific types below carry no reason of their own.
        if (e instanceof FileSystemException fileError && fileError.getReason() != null) {
            String reason = fileError.getReason().trim();
            // e.g. "Is a directory" reads better mid-sentence as "is a directory".
            return reason.substring(0, 1).toLowerCase() + reason.substring(1);
        }
        if (e instanceof AccessDeniedException) {
            return "permission was denied";
        }
        if (e instanceof FileAlreadyExistsException) {
            return "something that isn't a folder is in the way";
        }
        if (e instanceof NoSuchFileException) {
            return "it isn't there";
        }
        return "the file couldn't be opened";
    }

    /**
     * Turns one saved line back into the task it describes.
     *
     * @param line one line of the save file
     * @return the task that line stands for
     * @throws BillyException if the line is not in the expected format
     */
    private static Task parseTask(String line) throws BillyException {
        String type = line.split(FIELD_PATTERN, 2)[0].trim();

        // The type letter says how many fields the line should have. Splitting with
        // that limit means a '|' the user typed in the last field stays part of it
        // rather than being mistaken for a separator.
        String[] fields;
        Task task;
        switch (type) {
            case Todo.TYPE_LETTER -> {
                fields = splitFields(line, Todo.FIELD_COUNT);
                task = new Todo(fields[2]);
            }
            case Deadline.TYPE_LETTER -> {
                fields = splitFields(line, Deadline.FIELD_COUNT);
                task = new Deadline(fields[2], TaskDate.parse(fields[3]));
            }
            case Event.TYPE_LETTER -> {
                fields = splitFields(line, Event.FIELD_COUNT);
                task = new Event(fields[2], TaskDate.parse(fields[3]),
                        TaskDate.parse(fields[4]));
            }
            default -> throw new BillyException("unknown task type: " + type);
        }

        if (fields[1].equals(Task.FLAG_DONE)) {
            task.markAsDone();
        } else if (!fields[1].equals(Task.FLAG_NOT_DONE)) {
            throw new BillyException("the done flag must be " + Task.FLAG_NOT_DONE + " or "
                    + Task.FLAG_DONE + ", but was: " + fields[1]);
        }
        return task;
    }

    /**
     * Splits one saved line into its fields, checking that none are missing.
     *
     * @param line one line of the save file
     * @param fieldCount how many fields a line of this type should have
     * @return the trimmed fields
     * @throws BillyException if there are too few fields, or one of them is empty
     */
    private static String[] splitFields(String line, int fieldCount) throws BillyException {
        String[] fields = line.split(FIELD_PATTERN, fieldCount);
        if (fields.length < fieldCount) {
            throw new BillyException("expected " + fieldCount + " fields but found "
                    + fields.length);
        }
        // The split limit caps the count and the check above sets the floor, which
        // is what lets parseTask reach for fields[3] and fields[4] by number.
        assert fields.length == fieldCount
                : "expected exactly " + fieldCount + " fields but found " + fields.length;
        for (int i = 0; i < fields.length; i++) {
            fields[i] = fields[i].trim();
            if (fields[i].isEmpty()) {
                throw new BillyException("field " + (i + 1) + " is empty");
            }
        }
        return fields;
    }
}
