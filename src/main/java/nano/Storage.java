package nano;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import nano.task.Deadline;
import nano.task.Event;
import nano.task.Priority;
import nano.task.Task;
import nano.task.TaskList;
import nano.task.Todo;

/**
 * Stores and loads Nano tasks from a file.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates a storage object using the specified file path.
     *
     * @param filePath path of the file used to store tasks.
     */
    public Storage(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    /**
     * Loads all tasks from the data file.
     *
     * @return The tasks loaded from the data file.
     * @throws NanoException If the data file cannot be read.
     */
    public TaskList load() throws NanoException {
        if (!Files.exists(filePath)) {
            return new TaskList();
        }

        try {
            return loadTasks(Files.readAllLines(filePath));
        } catch (IOException e) {
            throw new NanoException("Unable to load tasks.");
        }
    }

    /**
     * Converts non-empty storage lines into a task list.
     *
     * @param lines lines read from the storage file.
     * @return tasks represented by the lines.
     * @throws NanoException if a line contains invalid task data.
     */
    private TaskList loadTasks(List<String> lines) throws NanoException {
        TaskList tasks = new TaskList();

        for (String line : lines) {
            if (!line.isBlank()) {
                tasks.add(parseStoredTask(line));
            }
        }

        return tasks;
    }

    /**
     * Parses one serialized task line.
     *
     * @param line serialized task data.
     * @return the task represented by the line.
     * @throws NanoException if the line is malformed.
     */
    private Task parseStoredTask(String line) throws NanoException {
        String[] parts = line.split(" \\| ", -1);
        validateParts(parts);

        Task task = createTask(parts);
        restoreTaskState(task, parts);
        return task;
    }

    /**
     * Creates a task from its validated storage fields.
     *
     * @param parts fields from a serialized task line.
     * @return the newly created task.
     * @throws NanoException if the task type is invalid.
     */
    private Task createTask(String[] parts) throws NanoException {
        String description = parts[3];

        return switch (parts[0]) {
            case "T" -> new Todo(description);
            case "D" -> new Deadline(description, parts[4]);
            case "E" -> new Event(description, parts[4], parts[5]);
            default -> throw new NanoException(
                    "Invalid task type in data file.");
        };
    }

    /**
     * Restores completion status and priority from storage fields.
     *
     * @param task task whose state should be restored.
     * @param parts fields containing status and priority.
     * @throws NanoException if either state field is invalid.
     */
    private void restoreTaskState(Task task, String[] parts)
            throws NanoException {
        if (parts[1].equals("1")) {
            task.markDone();
        } else if (!parts[1].equals("0")) {
            throw new NanoException("Invalid task status in data file.");
        }

        try {
            task.setPriority(Priority.valueOf(parts[2].toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new NanoException("Invalid priority in data file.");
        }
    }

    /**
     * Checks that a serialized task has the expected number of fields.
     *
     * @param parts fields from a serialized task line.
     * @throws NanoException if the task type or field count is invalid.
     */
    private void validateParts(String[] parts) throws NanoException {
        int expectedParts = switch (parts[0]) {
            case "T" -> 4;
            case "D" -> 5;
            case "E" -> 6;
            default -> throw new NanoException(
                    "Invalid task type in data file.");
        };

        if (parts.length != expectedParts) {
            throw new NanoException("Invalid task data in file.");
        }
    }

    /**
     * Saves the given tasks to the data file.
     *
     * @param tasks tasks to save.
     * @throws NanoException if the tasks cannot be saved.
     */
    public void save(TaskList tasks) throws NanoException {
        try {
            Path parent = filePath.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            List<String> lines = tasks.getTasks().stream()
                    .map(Task::toStorageString)
                    .toList();

            Files.write(filePath, lines);

        } catch (IOException e) {
            throw new NanoException("Unable to save tasks.");
        }
    }
}
