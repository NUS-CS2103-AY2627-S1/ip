package caesar.storage;

import caesar.exception.CaesarException;
import caesar.task.Deadline;
import caesar.task.Event;
import caesar.task.Task;
import caesar.task.ToDo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Loads tasks from and saves tasks to Caesar's task file.
 *
 * <p>Storage owns the file format and file-system details, but does not print
 * user-facing messages. This keeps persistence independent from the command
 * loop and makes it possible to replace the storage mechanism later.</p>
 */
public class Storage {
    private final Path filePath;
    private boolean fileWasCreated;

    /** Creates storage for the supplied task-file path. */
    public Storage(String filePath) {
        this(Paths.get(filePath));
    }

    /** Creates storage for the supplied task-file path. */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /** Returns the path used by this storage instance. */
    public Path getFilePath() {
        return filePath;
    }

    /**
     * Loads all tasks from the file, creating the file and its parent directory
     * when they do not exist.
     *
     * @return tasks read from the file
     * @throws CaesarException if the file cannot be read or contains invalid data
     */
    public ArrayList<Task> load() throws CaesarException {
        ArrayList<Task> tasks = new ArrayList<>();
        File file = filePath.toFile();
        fileWasCreated = false;
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            if (!file.exists()) {
                file.createNewFile();
                fileWasCreated = true;
            }

            try (Scanner fileScanner = new Scanner(file)) {
                int lineNumber = 0;
                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();
                    lineNumber++;
                    if (!line.isBlank()) {
                        tasks.add(parseTask(line, lineNumber));
                    }
                }
            }
        } catch (FileNotFoundException exception) {
            throw new CaesarException("Failed to read task file " + filePath + ": " + exception.getMessage());
        } catch (IOException exception) {
            throw new CaesarException("Failed to read task file " + filePath + ": " + exception.getMessage());
        }
        return tasks;
    }

    /** Returns whether the most recent load created a missing task file. */
    public boolean wasFileCreated() {
        return fileWasCreated;
    }

    /** Saves the complete task list, replacing the previous file contents. */
    public void save(Iterable<Task> tasks) throws CaesarException {
        Path parent = filePath.getParent();
        try {
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (FileWriter writer = new FileWriter(filePath.toFile())) {
                for (Task task : tasks) {
                    writer.write(serializeTask(task));
                    writer.write(System.lineSeparator());
                }
            }
        } catch (IOException exception) {
            throw new CaesarException("Failed to save tasks to file " + filePath + ": " + exception.getMessage());
        }
    }

    /** Converts a task into the pipe-delimited format stored in the task file. */
    private static String serializeTask(Task task) {
        String status = task.isDone() ? "1" : "0";
        if (task instanceof Deadline deadline) {
            return String.join(" | ", "D", status, task.getDescription(), deadline.getBy());
        }
        if (task instanceof Event event) {
            return String.join(" | ", "E", status, task.getDescription(), event.getStart(), event.getEnd());
        }
        return String.join(" | ", "T", status, task.getDescription());
    }

    /** Converts one stored line into a task, reporting malformed fields with its line number. */
    private static Task parseTask(String line, int lineNumber) throws CaesarException {
        String[] parts = line.split("\\|", -1);
        String type = parts.length > 0 ? parts[0].trim() : "";
        int expectedParts = switch (type) {
            case "T" -> 3;
            case "D" -> 4;
            case "E" -> 5;
            default -> throw invalidTaskLine(lineNumber, "unknown task type " + type);
        };
        if (parts.length != expectedParts) {
            throw invalidTaskLine(lineNumber, "expected " + expectedParts + " fields but found " + parts.length);
        }
        // The format-specific field count check must hold before fields are read.
        assert parts.length == expectedParts : "A parsed task must have its expected number of fields";

        boolean isDone = parseStatus(parts[1].trim(), lineNumber);
        String description = requireFileField(parts[2], "description", lineNumber);
        Task task;
        if ("T".equals(type)) {
            task = new ToDo(description);
        } else if ("D".equals(type)) {
            task = new Deadline(description, requireFileField(parts[3], "deadline", lineNumber));
        } else {
            task = new Event(description,
                    requireFileField(parts[3], "event start", lineNumber),
                    requireFileField(parts[4], "event end", lineNumber));
        }

        if (isDone) {
            task.markAsDone();
            // A stored completion flag of 1 must produce a completed task.
            assert task.isDone() : "A task loaded with status 1 must be complete";
        } else {
            // A newly created task loaded with status 0 must remain pending.
            assert !task.isDone() : "A task loaded with status 0 must be pending";
        }
        return task;
    }

    /** Converts the stored completion flag into a boolean value. */
    private static boolean parseStatus(String status, int lineNumber) throws CaesarException {
        if ("1".equals(status)) {
            return true;
        }
        if ("0".equals(status)) {
            return false;
        }
        throw invalidTaskLine(lineNumber, "status must be 1 or 0");
    }

    /** Validates and trims a required field read from the task file. */
    private static String requireFileField(String value, String fieldName, int lineNumber) throws CaesarException {
        String trimmedValue = value.trim();
        if (trimmedValue.isEmpty()) {
            throw invalidTaskLine(lineNumber, fieldName + " cannot be empty");
        }
        return trimmedValue;
    }

    /** Creates the standard exception for an invalid task-file line. */
    private static CaesarException invalidTaskLine(int lineNumber, String reason) {
        return new CaesarException("Invalid task data on line " + lineNumber + ": " + reason);
    }
}
