package penny.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import penny.common.PennyException;
import penny.task.Deadline;
import penny.task.Event;
import penny.task.Task;
import penny.task.TaskList;
import penny.task.Todo;

/**
 * Handles persistent storage of task data on the hard disk.
 * Encapsulates reading from and writing to disk, converting low-level I/O errors into domain exceptions.
 */
public class Storage {

    private static final String DELIMITER_REGEX = " \\| ";

    private final Path filePath;

    /**
     * Constructs a Storage object configured with a file path string.
     *
     * @param filePath The path of the file.
     */
    public Storage(String filePath) {
        this(filePath, new String[0]);
    }

    /**
     * Constructs a Storage object configured with an OS-independent relative path.
     *
     * @param first The primary directory or path segment.
     * @param more Additional path segments if any.
     */
    public Storage(String first, String... more) {
        this.filePath = Paths.get(first, more);
    }

    /**
     * Constructs a Storage object with a specified {@link Path}.
     *
     * @param filePath The {@link Path} where tasks will be stored.
     */
    public Storage(Path filePath) {
        assert filePath != null : "Storage filePath must not be null";
        this.filePath = filePath;
    }

    /**
     * Saves the tasks from a {@link TaskList} to the storage file on the hard disk.
     *
     * @param taskList The TaskList to save.
     * @throws PennyException If an I/O or security error occurs when writing to the file.
     */
    public void save(TaskList taskList) throws PennyException {
        assert taskList != null : "TaskList to save must not be null";
        save(taskList.getAllTasks());
    }

    /**
     * Saves the list of tasks to the storage file on the hard disk.
     * Creates any missing parent directories automatically before writing.
     *
     * @param tasks The list of tasks to save.
     * @throws PennyException If an I/O or security error occurs when writing to the file.
     */
    public void save(List<Task> tasks) throws PennyException {
        assert tasks != null : "Tasks list to save must not be null";
        assert this.filePath != null : "Storage filePath must not be null";

        try {
            if (filePath.getParent() != null && !Files.exists(filePath.getParent())) {
                Files.createDirectories(filePath.getParent());
            }

            List<String> lines = tasks.stream()
                    .map(task -> {
                        assert task != null : "Tasks list should not contain null elements";
                        String formattedLine = task.toFileFormat();
                        assert !formattedLine.isEmpty() : "Task file format representation must not be empty";
                        return formattedLine;
                    })
                    .toList();

            Files.write(filePath, lines);
        } catch (IOException | SecurityException e) {
            throw new PennyException("Failed to save tasks to file: " + e.getMessage());
        }
    }

    /**
     * Loads the saved tasks from the storage file on the hard disk.
     * If the storage file does not exist, returns an empty list cleanly.
     *
     * @return List of tasks loaded from disk.
     * @throws PennyException If an I/O error occurs or the file contents are corrupted.
     */
    public List<Task> load() throws PennyException {
        assert this.filePath != null : "Storage filePath must not be null";
        List<Task> tasks = new ArrayList<>();
        if (Files.notExists(filePath)) {
            return tasks;
        }

        try {
            List<String> lines = Files.readAllLines(filePath);
            for (String line : lines) {
                String trimmedLine = line.trim();
                if (trimmedLine.isEmpty()) {
                    continue;
                }
                tasks.add(parseTask(trimmedLine));
            }
        } catch (IOException | SecurityException e) {
            throw new PennyException("Could not read storage file: " + e.getMessage());
        }
        return tasks;
    }

    /**
     * Parses a single line from the storage file into a Task object.
     *
     * @param line The pipe-delimited line representing a saved task.
     * @return The parsed Task object (Todo, Deadline, or Event).
     * @throws PennyException If the line format is invalid, missing fields, or contains an unknown task type.
     */
    private Task parseTask(String line) throws PennyException {
        assert line != null : "Line to parse must not be null";
        String[] parts = line.split(DELIMITER_REGEX);
        if (parts.length < 3) {
            throw new PennyException("Corrupted format in storage file (insufficient fields): " + line);
        }

        String type = parts[0].trim();
        boolean isDone = parseStatus(parts[1].trim(), line);
        String description = parseDescription(parts[2].trim(), line);

        Task task = createTask(type, description, parts, line);
        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    private boolean parseStatus(String statusStr, String line) throws PennyException {
        if (statusStr.equals(Task.STATUS_DONE)) {
            return true;
        } else if (statusStr.equals(Task.STATUS_UNDONE)) {
            return false;
        }
        throw new PennyException("Invalid status indicator '" + statusStr + "' in storage file: " + line);
    }

    private String parseDescription(String description, String line) throws PennyException {
        if (description.isEmpty()) {
            throw new PennyException("Missing task description in storage file: " + line);
        }
        return description;
    }

    private Task createTask(String type, String description, String[] parts, String line)
            throws PennyException {
        switch (type) {
            case Todo.TYPE_CODE:
                return new Todo(description);
            case Deadline.TYPE_CODE:
                return createDeadline(description, parts, line);
            case Event.TYPE_CODE:
                return createEvent(description, parts, line);
            default:
                throw new PennyException("Unknown task type '" + type + "' in storage file: " + line);
        }
    }

    private Deadline createDeadline(String description, String[] parts, String line)
            throws PennyException {
        if (parts.length < 4 || parts[3].trim().isEmpty()) {
            throw new PennyException("Corrupted deadline task (missing deadline time): " + line);
        }
        return new Deadline(description, parts[3].trim());
    }

    private Event createEvent(String description, String[] parts, String line)
            throws PennyException {
        if (parts.length < 5 || parts[3].trim().isEmpty() || parts[4].trim().isEmpty()) {
            throw new PennyException("Corrupted event task (missing start or end time): " + line);
        }
        return new Event(description, parts[3].trim(), parts[4].trim());
    }
}
