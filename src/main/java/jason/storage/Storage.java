package jason.storage;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import jason.task.Deadline;
import jason.task.Event;
import jason.task.Task;
import jason.task.TaskList;
import jason.task.ToDo;

/**
 * Handles loading tasks from and saving tasks to a file.
 */
public class Storage {
    private static final String TODO_TYPE = "T";
    private static final String DEADLINE_TYPE = "D";
    private static final String EVENT_TYPE = "E";
    private static final String COMPLETED_STATUS = "1";
    private static final String INCOMPLETE_STATUS = "0";
    private final Path filePath;

    /**
     * Creates storage backed by the supplied relative or absolute path.
     *
     * @param filePath relative or absolute path of the save file.
     * @throws IllegalArgumentException if the path is null or blank.
     */
    public Storage(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("The storage file path cannot be empty.");
        }
        this.filePath = Path.of(filePath);
    }

    /**
     * Loads all valid tasks, returning an empty list when the file does not exist.
     *
     * @return valid tasks read from the save file.
     */
    public List<Task> load() {
        List<Task> tasks = new ArrayList<>();
        try {
            for (String line : Files.readAllLines(filePath)) {
                Task task = fromSaveFormat(line);
                if (task != null) {
                    tasks.add(task);
                }
            }
        } catch (NoSuchFileException e) {
            // No file is expected on the first run.
        } catch (IOException | SecurityException e) {
            reportError("load", e);
        }
        return tasks;
    }

    /**
     * Saves the current task list, creating its parent directory when needed.
     *
     * @param tasks task list to save.
     * @throws IllegalArgumentException if the task list is null.
     */
    public void save(TaskList tasks) {
        if (tasks == null) {
            throw new IllegalArgumentException("The task list cannot be null.");
        }
        try {
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }
            try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                for (int i = 1; i <= tasks.size(); i++) {
                    Task task = tasks.get(i);
                    assert task != null : "TaskList must not contain null tasks";
                    writer.write(toSaveFormat(task));
                    writer.newLine();
                }
            }
        } catch (IOException | SecurityException e) {
            reportError("save", e);
        }
    }

    /**
     * Converts a task into the line format used by the save file.
     */
    private String toSaveFormat(Task task) {
        String status = task.isCompleted() ? COMPLETED_STATUS : INCOMPLETE_STATUS;
        if (task instanceof ToDo todo && todo.getDuration() != null) {
            return String.format("%s | %s | %s | %d", TODO_TYPE, status,
                    task.getDescription(), todo.getDuration().toMinutes());
        }
        if (task instanceof Deadline deadline) {
            return String.format("%s | %s | %s | %s", DEADLINE_TYPE, status,
                    task.getDescription(), deadline.getDeadline());
        }
        if (task instanceof Event event) {
            return String.format("%s | %s | %s | %s | %s", EVENT_TYPE, status,
                    task.getDescription(), event.getStartDate(), event.getEndDate());
        }
        return String.format("%s | %s | %s", TODO_TYPE, status, task.getDescription());
    }

    /**
     * Parses one saved line, returning null when the record is malformed.
     */
    private Task fromSaveFormat(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }
        String[] fields = line.split("\\s*\\|\\s*", -1);
        if (fields.length < 3) {
            return null;
        }

        String type = fields[0].trim();
        String status = fields[1].trim();
        String description = fields[2].trim();
        if (description.isEmpty()
                || !(status.equals(INCOMPLETE_STATUS) || status.equals(COMPLETED_STATUS))) {
            return null;
        }

        Task task = switch (type) {
            case TODO_TYPE -> parseTodoRecord(fields, description);
            case DEADLINE_TYPE -> parseDeadlineRecord(fields, description);
            case EVENT_TYPE -> parseEventRecord(fields, description);
            default -> null;
        };
        if (task == null) {
            return null;
        }
        if (status.equals(COMPLETED_STATUS)) {
            task.markComplete();
        }
        return task;
    }

    /**
     * Parses a todo record from its storage fields.
     *
     * @param fields fields split from a saved record.
     * @param description task description.
     * @return parsed todo task, or null for an invalid record.
     */
    private Task parseTodoRecord(String[] fields, String description) {
        if (fields.length == 3) {
            return new ToDo(description);
        }
        if (fields.length != 4 || fields[3].trim().isEmpty()) {
            return null;
        }
        try {
            return new ToDo(description, Duration.ofMinutes(Long.parseLong(fields[3].trim())));
        } catch (NumberFormatException | ArithmeticException e) {
            return null;
        }
    }

    /**
     * Parses a deadline record from its storage fields.
     *
     * @param fields fields split from a saved record.
     * @param description task description.
     * @return parsed deadline task, or null for an invalid record.
     */
    private Task parseDeadlineRecord(String[] fields, String description) {
        if (fields.length != 4 || fields[3].trim().isEmpty()) {
            return null;
        }
        try {
            return new Deadline(description, fields[3].trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Parses an event record from its storage fields.
     *
     * @param fields fields split from a saved record.
     * @param description task description.
     * @return parsed event task, or null for an invalid record.
     */
    private Task parseEventRecord(String[] fields, String description) {
        if (fields.length != 5 || fields[3].trim().isEmpty()
                || fields[4].trim().isEmpty()) {
            return null;
        }
        try {
            return new Event(description, parseDateTime(fields[3].trim()),
                    parseDateTime(fields[4].trim()));
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Parses a saved date-time, accepting older date-only records as midnight.
     */
    private LocalDateTime parseDateTime(String value) {
        try {
            return LocalDateTime.parse(value);
        } catch (DateTimeParseException e) {
            return LocalDate.parse(value).atStartOfDay();
        }
    }

    /**
     * Reports an I/O or permission problem without terminating the application.
     */
    private void reportError(String operation, Exception exception) {
        System.err.printf("Warning: unable to %s tasks at %s (%s)%n",
                operation, filePath, exception.getMessage());
    }
}
