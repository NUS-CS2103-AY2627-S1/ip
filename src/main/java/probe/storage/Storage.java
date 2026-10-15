package probe.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import probe.task.Deadline;
import probe.task.Event;
import probe.task.Task;
import probe.task.Todo;

/**
 * Loads tasks from and saves tasks to a local text file.
 */
public class Storage {
    private static final String TODO_TYPE = "T";
    private static final String DEADLINE_TYPE = "D";
    private static final String EVENT_TYPE = "E";
    private static final String INCOMPLETE_STATUS = "0";
    private static final String COMPLETE_STATUS = "1";
    private final Path filePath;

    /**
     * Creates storage backed by the specified file path.
     *
     * @param filePath Path of the storage file.
     */
    public Storage(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    /**
     * Returns valid tasks loaded from the file, skipping malformed lines.
     *
     * @return Tasks loaded from storage.
     */
    public List<Task> load() {
        List<Task> tasks = new ArrayList<>();
        File file = filePath.toFile();

        if (!file.exists()) {
            System.out.println("Data file not found. Starting with an empty task list.");
            return tasks;
        }

        try (Scanner scanner = new Scanner(file)) {
            int lineNumber = 0;
            while (scanner.hasNextLine()) {
                lineNumber++;
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }

                try {
                    Task task = parseTask(line);
                    tasks.add(task);
                } catch (IllegalArgumentException e) {
                    System.out.println("Warning: Corrupted data at line " + lineNumber 
                            + " (" + e.getMessage() + "). Skipping line.");
                }
            }
        } catch (IOException | SecurityException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        return tasks;
    }

    /**
     * Saves the supplied tasks, creating parent directories when needed.
     *
     * @param tasks Tasks to save.
     */
    public void save(List<Task> tasks) {
        try {
            Path parentDir = filePath.getParent();
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }

            try (FileWriter writer = new FileWriter(filePath.toFile())) {
                for (Task task : tasks) {
                    writer.write(task.toFileFormat() + System.lineSeparator());
                }
            }
        } catch (IOException | SecurityException e) {
            System.out.println("Error saving tasks to disk: " + e.getMessage());
        }
    }

    /**
     * Returns a task converted from one storage-format line.
     *
     * @param line Storage-format line to parse.
     * @return Parsed task.
     * @throws IllegalArgumentException If the line is malformed.
     */
    private Task parseTask(String line) {
        String[] parts = line.split("\\s*\\|\\s*");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Insufficient fields");
        }

        String type = parts[0];
        if (!type.equals(TODO_TYPE) && !type.equals(DEADLINE_TYPE)
                && !type.equals(EVENT_TYPE)) {
            throw new IllegalArgumentException("Unknown task type: " + type);
        }

        if (!parts[1].equals(INCOMPLETE_STATUS) && !parts[1].equals(COMPLETE_STATUS)) {
            throw new IllegalArgumentException("Invalid completion status: " + parts[1]);
        }

        int baseFields = switch (type) {
            case TODO_TYPE -> 3;
            case DEADLINE_TYPE -> 4;
            case EVENT_TYPE -> 5;
            default -> throw new IllegalArgumentException("Unknown task type: " + type);
        };
        if (parts.length != baseFields && parts.length != baseFields + 1) {
            throw new IllegalArgumentException("Invalid number of fields for task type " + type);
        }
        assert parts.length == baseFields || parts.length == baseFields + 1
                : "A validated storage record must have the expected number of fields";

        boolean isDone = parts[1].equals(COMPLETE_STATUS);
        String description = parts[2];
        if (description.isBlank()) {
            throw new IllegalArgumentException("Task description cannot be empty");
        }

        Task task;
        switch (type) {
            case TODO_TYPE:
                task = new Todo(description);
                break;
            case DEADLINE_TYPE:
                if (parts[3].isBlank()) {
                    throw new IllegalArgumentException("Deadline date cannot be empty");
                }
                task = new Deadline(description, LocalDateTime.parse(parts[3]));
                break;
            case EVENT_TYPE:
                if (parts[3].isBlank() || parts[4].isBlank()) {
                    throw new IllegalArgumentException("Event times cannot be empty");
                }
                task = new Event(description, LocalDateTime.parse(parts[3]),
                        LocalDateTime.parse(parts[4]));
                break;
            default:
                throw new IllegalArgumentException("Unknown task type: " + type);
        }

        if (isDone) {
            task.markAsDone();
        }
        if (parts.length == baseFields + 1) {
            String[] savedTags = parts[baseFields].split(",");
            task.addTag(savedTags);
        }
        assert task != null : "A recognized task type must produce a task";
        return task;
    }
}
