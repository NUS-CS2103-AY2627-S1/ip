package bob.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import bob.exception.BobException;
import bob.task.Deadline;
import bob.task.Event;
import bob.task.Task;
import bob.task.TaskList;
import bob.task.ToDo;
import bob.util.DatetimeHelper;

/**
 * Provides persistent storage for a list of {@link Task} objects.
 * <p>
 * Tasks are stored in a text file using a type-specific format:
 * </p>
 *
 * <pre>
 * {@link ToDo}:      T &lt;0x01&gt; status &lt;0x01&gt; name
 * {@link Deadline}:  D &lt;0x01&gt; status &lt;0x01&gt; name &lt;0x01&gt; deadline
 * {@link Event}:     E &lt;0x01&gt; status &lt;0x01&gt; name &lt;0x01&gt; from &lt;0x01&gt; to
 * </pre>
 * <p>
 * The storage file is located at {@code ./data/tasks.txt}.
 * If the file does not exist when loading, an empty task list is returned.
 * </p>
 */
public class TaskStorage implements Storage<TaskList> {

    private static final Path FILE_PATH = Paths.get("data", "tasks.txt");

    private final Path path;
    private final TaskStorageParser parser = new TaskStorageParser();
    private final List<String> invalidTaskLines = new ArrayList<>();

    /**
     * Constructs a TaskStorage instance using the default storage path
     * ({@code data/tasks.txt}).
     */
    public TaskStorage() {
        this(FILE_PATH);
    }

    /**
     * Constructs a TaskStorage instance with a custom file path.
     *
     * @param path the path to the storage file
     */
    public TaskStorage(Path path) {
        assert path != null : "Storage file path cannot be null";
        this.path = path;
    }

    /**
     * Loads the tasks from the persistent storage file.
     *
     * Invalid task lines are omitted, and the storage file is rewritten without
     * them so that later loads do not encounter the same corruption.
     *
     * @return a {@link TaskList} containing all valid parsed tasks, or an empty list
     *         if the file does not exist
     * @throws BobException if an I/O error occurs while reading or cleaning the file
     */
    @Override
    public TaskList load() throws BobException {
        invalidTaskLines.clear();

        // No file on first startup -> return empty task list.
        if (!Files.exists(path)) {
            return new TaskList();
        }

        List<String> lines = readTaskLines();
        TaskList validTasks = parseStoredTasks(lines);
        removeInvalidTaskLines(validTasks);
        return validTasks;
    }

    /**
     * Reads all lines from the storage file.
     *
     * @return the lines read from storage
     * @throws BobException if the file cannot be read
     */
    private List<String> readTaskLines() throws BobException {
        try {
            return Files.readAllLines(path);
        } catch (IOException e) {
            throw new BobException("I/O Error: Unable to load tasks from storage");
        }
    }

    /**
     * Parses valid tasks and records invalid storage lines.
     *
     * @param lines the storage lines to parse
     * @return the tasks parsed from valid lines
     */
    private TaskList parseStoredTasks(List<String> lines) {
        List<Task> validTasks = new ArrayList<>();
        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }

            try {
                validTasks.add(parser.parse(line));
            } catch (BobException e) {
                invalidTaskLines.add(line);
            }
        }
        return new TaskList(validTasks);
    }

    /**
     * Rewrites storage without invalid lines when corruption was detected.
     *
     * @param validTasks the tasks that remain valid
     * @throws BobException if the cleaned tasks cannot be saved
     */
    private void removeInvalidTaskLines(TaskList validTasks) throws BobException {
        if (!invalidTaskLines.isEmpty()) {
            save(validTasks);
        }
    }

    /**
     * Returns the number of invalid task lines discarded during the most recent load.
     *
     * @return the number of discarded storage lines
     */
    public int getInvalidTaskCount() {
        return invalidTaskLines.size();
    }

    /**
     * Saves the given task list to the storage file.
     *
     * @param tasks the list of tasks to save
     * @throws BobException if an I/O error occurs during saving
     */
    @Override
    public void save(TaskList tasks) throws BobException {
        assert tasks != null : "TaskList to save cannot be null";
        try {
            // Create ./data/ if it does not exist.
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }

            List<String> lines = new ArrayList<>();

            for (Task task : tasks) {
                lines.add(task.export());
            }

            Files.write(path, lines);

        } catch (IOException e) {
            throw new BobException("I/O Error: Unable to save tasks");
        }
    }

}

/**
 * Parses tasks from their persistent storage representation.
 */
class TaskStorageParser {
    private static final String TYPE_TODO = "T";
    private static final String TYPE_DEADLINE = "D";
    private static final String TYPE_EVENT = "E";
    private static final String STATUS_DONE = "1";
    private static final String STATUS_NOT_DONE = "0";

    /**
     * Parses a single storage line into a task.
     *
     * @param line the storage line to parse
     * @return the parsed task
     * @throws BobException if the storage line is invalid
     */
    Task parse(String line) throws BobException {
        assert line != null && !line.isBlank() : "Line to parse should not be null or blank";
        String[] parts = line.split(Character.toString(Task.STORAGE_DELIMITER), -1);

        if (parts.length < 3) {
            throw new BobException("Error: Invalid task export format: " + line);
        }

        boolean isDone = parseDoneStatus(parts[1], line);
        Task task = parseTaskByType(parts, line);
        markTaskIfDone(task, isDone);
        return task;
    }

    private Task parseTaskByType(String[] parts, String line) throws BobException {
        switch (parts[0]) {
            case TYPE_TODO:
                return parseTodo(parts);

            case TYPE_DEADLINE:
                return parseDeadline(parts, line);

            case TYPE_EVENT:
                return parseEvent(parts, line);

            default:
                throw new BobException("Error: Unknown task type of " + parts[0]);
        }
    }

    private boolean parseDoneStatus(String status, String line) throws BobException {
        if (status.equals(STATUS_DONE)) {
            return true;
        }
        if (status.equals(STATUS_NOT_DONE)) {
            return false;
        }
        throw new BobException("Error: Invalid done status: " + line);
    }

    private void markTaskIfDone(Task task, boolean isDone) {
        if (isDone) {
            task.mark();
        }
    }

    private Task parseTodo(String[] parts) {
        return new ToDo(parts[2]);
    }

    private Task parseDeadline(String[] parts, String line) throws BobException {
        if (parts.length != 4) {
            throw new BobException("Error: Corrupted deadline format: " + line);
        }

        try {
            LocalDateTime deadline = LocalDateTime.parse(parts[3], DatetimeHelper.ISO_FORMATTER);
            return new Deadline(parts[2], deadline);
        } catch (DateTimeParseException e) {
            throw new BobException("Error: Corrupted date-time format: " + line);
        }
    }

    private Task parseEvent(String[] parts, String line) throws BobException {
        if (parts.length != 5) {
            throw new BobException("Error: Corrupted event format: " + line);
        }

        try {
            LocalDateTime from = LocalDateTime.parse(parts[3], DatetimeHelper.ISO_FORMATTER);
            LocalDateTime to = LocalDateTime.parse(parts[4], DatetimeHelper.ISO_FORMATTER);
            return new Event(parts[2], from, to);
        } catch (DateTimeParseException e) {
            throw new BobException("Error: Corrupted date-time format: " + line);
        }
    }
}
