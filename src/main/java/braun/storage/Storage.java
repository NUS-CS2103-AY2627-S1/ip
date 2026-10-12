package braun.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import braun.exception.BraunException;
import braun.task.Deadline;
import braun.task.Event;
import braun.task.Task;
import braun.task.Todo;

/**
 * Handles loading tasks from and saving tasks to the local filesystem.
 * Uses OS-independent relative paths and manages missing files, missing directories,
 * and corrupted entries gracefully.
 */
public class Storage {

    private static final String DELIMITER_REGEX = " \\| ";
    private static final String TYPE_TODO = "T";
    private static final String TYPE_DEADLINE = "D";
    private static final String TYPE_EVENT = "E";
    private static final String STATUS_DONE = "1";
    private static final String STATUS_UNDONE = "0";

    private final Path filePath;

    /**
     * Constructs a new {@code Storage} handler for the specified file path string.
     * The path string is resolved using OS-independent {@link Paths#get(String, String...)}.
     *
     * @param filePath relative or absolute path string to the data file.
     */
    public Storage(String filePath) {
        assert filePath != null : "Storage filePath string must not be null.";
        this.filePath = Paths.get(filePath);
    }

    /**
     * Returns the resolved {@link Path} of the storage file.
     *
     * @return the storage file path.
     */
    public Path getFilePath() {
        return filePath;
    }

    /**
     * Loads tasks from the data file on the hard disk.
     * If the file or its parent directory does not exist, an empty list is returned.
     * Corrupted lines are skipped with a warning logged to {@code System.err}.
     *
     * @return a list of restored {@link Task} objects.
     */
    public ArrayList<Task> load() {
        ArrayList<Task> tasks = new ArrayList<>();

        if (Files.notExists(filePath)) {
            return tasks;
        }

        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                try {
                    Task task = parseTask(trimmed);
                    tasks.add(task);
                } catch (BraunException e) {
                    System.err.println("*static* Warning: Skipping corrupted log entry: "
                            + trimmed + " (" + e.getMessage() + ")");
                }
            }
        } catch (IOException e) {
            System.err.println("*static* Warning: Could not read broadcast log file: " + e.getMessage());
        }

        return tasks;
    }

    /**
     * Saves the current list of tasks to the storage file on the hard disk.
     * Automatically creates any missing parent directories before writing.
     *
     * @param tasks the list of tasks to save.
     * @throws BraunException if an I/O error occurs while creating directories or writing the file.
     */
    public void save(ArrayList<Task> tasks) throws BraunException {
        assert tasks != null : "Tasks collection to save cannot be null.";
        try {
            Path parentDir = filePath.getParent();
            if (parentDir != null && Files.notExists(parentDir)) {
                Files.createDirectories(parentDir);
            }

            List<String> lines = tasks.stream()
                    .map(Task::toFileFormat)
                    .collect(Collectors.toList());

            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new BraunException("*static* Failed to save broadcast log to disk: " + e.getMessage(), e);
        }
    }

    /**
     * Parses a single line from the data file into its corresponding {@link Task} instance.
     * The line must follow the pipe-delimited format:
     * {@code T | <0/1> | <description>} for Todo,
     * {@code D | <0/1> | <description> | <by>} for Deadline, or
     * {@code E | <0/1> | <description> | <from> | <to>} for Event.
     *
     * @param line the text line to parse.
     * @return the reconstructed {@link Task} object.
     * @throws BraunException if the line is corrupted or has missing fields.
     */
    public static Task parseTask(String line) throws BraunException {
        assert line != null : "Line to parse cannot be null.";
        String[] parts = line.split(DELIMITER_REGEX);
        if (parts.length < 3) {
            throw new BraunException("Insufficient fields in line");
        }

        String type = parts[0].trim();
        String status = parts[1].trim();
        String description = parts[2].trim();

        if (description.isEmpty()) {
            throw new BraunException("Task description is empty");
        }

        Task task;
        switch (type) {
        case TYPE_TODO:
            task = parseTodo(description);
            break;
        case TYPE_DEADLINE:
            task = parseDeadline(parts, description);
            break;
        case TYPE_EVENT:
            task = parseEvent(parts, description);
            break;
        default:
            throw new BraunException("Unknown task type identifier: " + type);
        }

        if (status.equals(STATUS_DONE)) {
            task.markAsDone();
        } else if (!status.equals(STATUS_UNDONE)) {
            throw new BraunException("Invalid task completion status: " + status);
        }

        assert task != null : "Parsed task instance must not be null.";
        return task;
    }

    private static Task parseTodo(String description) {
        return new Todo(description);
    }

    private static Task parseDeadline(String[] parts, String description) throws BraunException {
        if (parts.length < 4 || parts[3].trim().isEmpty()) {
            throw new BraunException("Deadline is missing due date/time");
        }
        return new Deadline(description, parts[3].trim());
    }

    private static Task parseEvent(String[] parts, String description) throws BraunException {
        if (parts.length < 5 || parts[3].trim().isEmpty() || parts[4].trim().isEmpty()) {
            throw new BraunException("Event is missing start or end date/time");
        }
        return new Event(description, parts[3].trim(), parts[4].trim());
    }
}
