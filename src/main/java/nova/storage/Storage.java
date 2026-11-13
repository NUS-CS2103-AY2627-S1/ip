package nova.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import nova.exception.NovaException;
import nova.task.Deadline;
import nova.task.Event;
import nova.task.Task;
import nova.task.Todo;

/**
 * Loads tasks from and saves tasks to a local data file.
 */
public class Storage {
    private static final String DELIMITER = " | ";

    private final Path filePath;

    /**
     * Creates a storage manager for the specified file.
     *
     * @param filePath Path of the task data file.
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Returns tasks loaded from the data file.
     *
     * @return Loaded tasks, or an empty list if the file does not exist.
     * @throws NovaException If the data cannot be read or parsed.
     */
    public List<Task> load() throws NovaException {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return tasks;
        }

        try {
            for (String line : Files.readAllLines(filePath)) {
                tasks.add(parseTask(line));
            }
            return tasks;
        } catch (IOException | RuntimeException exception) {
            throw new NovaException("I couldn't load the saved tasks.");
        }
    }

    /**
     * Saves all tasks to the data file.
     *
     * @param tasks Tasks to save.
     * @throws NovaException If the data cannot be written.
     */
    public void save(List<Task> tasks) throws NovaException {
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            List<String> lines = tasks.stream()
                    .map(Task::toDataString)
                    .toList();
            Files.write(filePath, lines);
        } catch (IOException exception) {
            throw new NovaException("I couldn't save the tasks.");
        }
    }

    /**
     * Parses a task stored as {@code type | status | description [| date...]}.
     *
     * @param line Serialized task data.
     * @return Parsed task.
     */
    private Task parseTask(String line) {
        String[] headerParts = line.split(" \\| ", 3);
        if (headerParts.length != 3
                || (!headerParts[1].equals("0") && !headerParts[1].equals("1"))) {
            throw new IllegalArgumentException("Invalid saved task");
        }

        Task task = switch (headerParts[0]) {
            case "T" -> new Todo(requireDescription(headerParts[2]));
            case "D" -> parseDeadline(headerParts[2]);
            case "E" -> parseEvent(headerParts[2]);
            default -> throw new IllegalArgumentException("Unknown task type");
        };
        if (headerParts[1].equals("1")) {
            task.markDone();
        }
        return task;
    }

    private Task parseDeadline(String taskData) {
        int dateSeparator = taskData.lastIndexOf(DELIMITER);
        if (dateSeparator <= 0 || dateSeparator + DELIMITER.length() >= taskData.length()) {
            throw new IllegalArgumentException("Invalid saved deadline");
        }

        String description = requireDescription(taskData.substring(0, dateSeparator));
        LocalDate dueDate = LocalDate.parse(taskData.substring(dateSeparator + DELIMITER.length()));
        return new Deadline(description, dueDate);
    }

    private Task parseEvent(String taskData) {
        int toSeparator = taskData.lastIndexOf(DELIMITER);
        int fromSeparator = taskData.lastIndexOf(DELIMITER, toSeparator - 1);
        if (fromSeparator <= 0
                || toSeparator <= fromSeparator + DELIMITER.length()
                || toSeparator + DELIMITER.length() >= taskData.length()) {
            throw new IllegalArgumentException("Invalid saved event");
        }

        String description = requireDescription(taskData.substring(0, fromSeparator));
        LocalDate from = LocalDate.parse(
                taskData.substring(fromSeparator + DELIMITER.length(), toSeparator));
        LocalDate to = LocalDate.parse(taskData.substring(toSeparator + DELIMITER.length()));
        if (!to.isAfter(from)) {
            throw new IllegalArgumentException("Invalid saved event date range");
        }
        return new Event(description, from, to);
    }

    private String requireDescription(String description) {
        if (description.isBlank()) {
            throw new IllegalArgumentException("Missing task description");
        }
        return description;
    }
}
