package suki;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Loads tasks from, and saves tasks to, a plain-text file on disk.
 *
 * <p>Each task occupies one line, with fields separated by " | ". The first
 * field is the task type icon, the second is 1 (done) or 0 (not done), and the
 * rest are type-specific. For example:
 * <pre>
 * T | 1 | read book
 * D | 0 | return book | Sunday
 * E | 0 | project meeting | Mon 2pm | 4pm
 * </pre>
 */
public class Storage {
    /** What separates the fields on each saved line. */
    private static final String SEPARATOR = " | ";

    /** Type code written for a todo. */
    private static final String TODO_CODE = "T";

    /** Type code written for a deadline. */
    private static final String DEADLINE_CODE = "D";

    /** Type code written for an event. */
    private static final String EVENT_CODE = "E";

    /** Field value meaning the task is done. */
    private static final String DONE_FLAG = "1";

    /** Field value meaning the task is not done. */
    private static final String NOT_DONE_FLAG = "0";

    /** Used when splitting, because "|" is a regex metacharacter. */
    private static final String SEPARATOR_REGEX = " \\| ";

    /** Where the save file lives, relative to the working directory. */
    private final String filePath;

    /**
     * Creates a Storage that reads and writes the given file.
     *
     * @param filePath where tasks are saved, relative to the working directory
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Reads the saved tasks from disk.
     *
     * <p>A missing file simply means there is nothing saved yet, so an empty
     * list is returned rather than an error. Individual lines that cannot be
     * understood are skipped so that one bad line does not lose the whole file.
     *
     * <p>Reading through a stream lets the two rules above be stated as the
     * two steps they are, decoding each line and then dropping the ones that
     * could not be decoded, instead of interleaving both with the mechanics of
     * advancing a scanner.
     *
     * @return the tasks that were saved, in the order they were saved
     * @throws SukiException if the file exists but cannot be read
     */
    public ArrayList<Task> load() throws SukiException {
        File file = new File(filePath);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        // The stream is closed by try-with-resources because Files#lines holds
        // the file open until the stream is consumed.
        try (Stream<String> lines = Files.lines(file.toPath())) {
            return lines.map(this::parseTask)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            throw new SukiException("I couldn't read your saved tasks: " + e.getMessage());
        }
    }

    /**
     * Writes the given tasks to disk, replacing whatever was there before.
     *
     * <p>The parent directory is created if it does not already exist, so the
     * user does not have to set anything up by hand.
     *
     * @param tasks the tasks to save
     * @throws SukiException if the file cannot be written
     */
    public void save(TaskList tasks) throws SukiException {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new SukiException("I couldn't create the folder " + parent + " to save your tasks.");
        }

        try (FileWriter writer = new FileWriter(file)) {
            for (Task task : tasks.asList()) {
                writer.write(encode(task) + System.lineSeparator());
            }
        } catch (IOException e) {
            throw new SukiException("I couldn't save your tasks: " + e.getMessage());
        }
    }

    /**
     * Turns a task into the single line of text that represents it on disk.
     *
     * @param task the task to encode
     * @return the line to write to the save file
     */
    private String encode(Task task) {
        assert task != null : "the task list never holds nulls, so nothing null reaches here";
        String common = task.getTypeIcon() + SEPARATOR
                + (task.isDone() ? DONE_FLAG : NOT_DONE_FLAG) + SEPARATOR
                + task.getDescription();
        if (task instanceof Deadline deadline) {
            return common + SEPARATOR + deadline.getBy().toStorageString();
        }
        if (task instanceof Event event) {
            return common + SEPARATOR + event.getFrom().toStorageString()
                    + SEPARATOR + event.getTo().toStorageString();
        }
        return common;
    }

    /**
     * Turns one saved line back into a task.
     *
     * @param line a single line read from the save file
     * @return the task, or null if the line is malformed and should be skipped
     */
    private Task parseTask(String line) {
        String[] parts = line.split(SEPARATOR_REGEX);
        // A short line means a corrupt save file, which is not a bug in Suki,
        // so it is skipped rather than asserted away.
        if (parts.length < 3) {
            return null;
        }
        assert parts.length >= 3 : "the guard above returns early for shorter lines";

        String typeIcon = parts[0];
        if (!parts[1].equals(DONE_FLAG) && !parts[1].equals(NOT_DONE_FLAG)) {
            return null;
        }
        boolean isDone = parts[1].equals(DONE_FLAG);
        String description = parts[2];

        Task task;
        try {
            switch (typeIcon) {
            case TODO_CODE:
                task = new Todo(description);
                break;
            case DEADLINE_CODE:
                if (parts.length < 4) {
                    return null;
                }
                task = new Deadline(description, DateTime.parse(parts[3]));
                break;
            case EVENT_CODE:
                if (parts.length < 5) {
                    return null;
                }
                task = new Event(description, DateTime.parse(parts[3]), DateTime.parse(parts[4]));
                break;
            default:
                return null;
            }
        } catch (SukiException e) {
            // An unreadable date means this line is corrupt; skip it rather
            // than abandoning the rest of the file.
            return null;
        }

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }
}
