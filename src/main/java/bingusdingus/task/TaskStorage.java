package bingusdingus.task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import bingusdingus.parser.DateTimeParser;

/** Saves and loads tasks using the Bingus Dingus storage format. */
public class TaskStorage {
    private static final String TASK_TYPE_TODO = "T";
    private static final String TASK_TYPE_DEADLINE = "D";
    private static final String TASK_TYPE_EVENT = "E";
    private static final String TASK_STATUS_INCOMPLETE = "0";
    private static final String TASK_STATUS_COMPLETE = "1";
    private static final Path STORAGE_PATH = Path.of(".", "data", "bingusdingus.txt");

    /** Saves the supplied tasks to disk, replacing the previous contents. */
    public void save(List<Task> tasks) {
        if (tasks == null) {
            throw new IllegalArgumentException("Tasks cannot be null");
        }
        try {
            Files.createDirectories(STORAGE_PATH.getParent());
            Files.write(STORAGE_PATH,
                    tasks.stream().map(Task::toFileFormat).collect(Collectors.toList()),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);
        } catch (IOException | SecurityException e) {
            throw new IllegalStateException("Unable to save tasks to " + STORAGE_PATH, e);
        }
    }

    /** Returns valid tasks loaded from disk, ignoring malformed records. */
    public List<Task> load() {
        if (!Files.exists(STORAGE_PATH)) {
            return List.of();
        }

        ArrayList<Task> loadedTasks = new ArrayList<>();
        try {
            for (String line : Files.readAllLines(STORAGE_PATH, StandardCharsets.UTF_8)) {
                Task task = parseStoredTask(line);
                if (task != null) {
                    loadedTasks.add(task);
                }
            }
        } catch (IOException | SecurityException e) {
            throw new IllegalStateException("Unable to load tasks from " + STORAGE_PATH, e);
        }
        return loadedTasks;
    }

    /** Parses one stored task line, returning null for malformed lines. */
    private Task parseStoredTask(String line) {
        String[] parts = normalizeStoredParts(line);
        if (parts == null) {
            return null;
        }

        Task task;
        try {
            task = createStoredTask(parts);
        } catch (DateTimeParseException | IllegalArgumentException e) {
            // Invalid typed values, including impossible event ranges, are ignored.
            task = null;
        }

        if (task != null && parts[1].equals(TASK_STATUS_COMPLETE)) {
            task.markAsDone();
        }
        return task;
    }

    /** Normalizes a stored record and returns null when its basic structure is invalid. */
    private String[] normalizeStoredParts(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }

        String[] parts = line.split("\\|", -1);
        if (parts.length < 3 || !isValidStatus(parts[1])) {
            return null;
        }

        parts[0] = parts[0].trim().replace("\uFEFF", "");
        for (int i = 1; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }
        return parts;
    }

    /** Returns whether a stored status represents an incomplete or completed task. */
    private boolean isValidStatus(String status) {
        String normalizedStatus = status.trim();
        return normalizedStatus.equals(TASK_STATUS_INCOMPLETE)
                || normalizedStatus.equals(TASK_STATUS_COMPLETE);
    }

    /** Creates a task from normalized stored fields, returning null for an invalid record. */
    private Task createStoredTask(String[] parts) {
        switch (parts[0]) {
            case TASK_TYPE_TODO:
                return parseStoredTodo(parts);
            case TASK_TYPE_DEADLINE:
                return parseStoredDeadline(parts);
            case TASK_TYPE_EVENT:
                return parseStoredEvent(parts);
            default:
                return null;
        }
    }

    /** Creates a todo task from normalized stored fields. */
    private Task parseStoredTodo(String[] parts) {
        if (parts.length != 3 || !hasNonBlankFields(parts, 2)) {
            return null;
        }
        return new Todo(parts[2]);
    }

    /** Creates a deadline task from normalized stored fields. */
    private Task parseStoredDeadline(String[] parts) {
        if (parts.length != 4 || !hasNonBlankFields(parts, 2)) {
            return null;
        }
        return new Deadline(parts[2], DateTimeParser.parseStorage(parts[3]));
    }

    /** Creates an event task from normalized stored fields. */
    private Task parseStoredEvent(String[] parts) {
        if (parts.length != 5 || !hasNonBlankFields(parts, 2)) {
            return null;
        }
        return new Event(parts[2], DateTimeParser.parseStorage(parts[3]),
                DateTimeParser.parseStorage(parts[4]));
    }

    /** Returns whether all stored fields from the specified index are non-blank. */
    private boolean hasNonBlankFields(String[] parts, int startIndex) {
        for (int i = startIndex; i < parts.length; i++) {
            if (parts[i].isBlank()) {
                return false;
            }
        }
        return true;
    }
}
