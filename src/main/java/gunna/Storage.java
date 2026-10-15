package gunna;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Scanner;

import gunna.task.Deadline;
import gunna.task.Event;
import gunna.task.Task;
import gunna.task.Todo;

/**
 * Handles saving and loading of tasks to/from the hard disk.
 * Tasks are saved in a pipe-separated format with proper escaping for special characters.
 */
public class Storage {
    private String filePath;

    /**
     * Creates a Storage instance with the specified file path.
     *
     * @param filePath The path to the data file relative to the project root.
     */
    public Storage(String filePath) {
        assert filePath != null : "File path cannot be null";
        assert !filePath.trim().isEmpty() : "File path cannot be empty";
        this.filePath = filePath;
    }

    /**
     * Escapes special characters in a string for safe storage.
     * Escapes pipe (|), backslash (\), and newline characters.
     *
     * @param text The text to escape.
     * @return The escaped text.
     */
    private String escapeText(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\\", "\\\\") // Escape backslash first
                   .replace("|", "\\|") // Escape pipe
                   .replace("\n", "\\n") // Escape newline
                   .replace("\r", "\\r"); // Escape carriage return
    }

    /**
     * Unescapes special characters from stored text.
     *
     * @param text The escaped text.
     * @return The unescaped text.
     */
    private String unescapeText(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\\r", "\r")
                   .replace("\\n", "\n")
                   .replace("\\|", "|")
                   .replace("\\\\", "\\");
    }

    /**
     * Loads tasks from the data file.
     * Returns an empty list if the file doesn't exist or if there's an error.
     * Handles corrupted lines gracefully by skipping them.
     *
     * @return ArrayList of tasks loaded from the file.
     */
    public ArrayList<Task> loadTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        File file = new File(filePath);

        // If file doesn't exist, return empty list
        if (!file.exists()) {
            return tasks;
        }

        // Check if file is readable
        if (!file.canRead()) {
            System.out.println("     Warning: Cannot read data file. Starting with empty task list.");
            return tasks;
        }

        try (Scanner scanner = new Scanner(file)) {
            int lineNumber = 0;
            while (scanner.hasNextLine()) {
                lineNumber++;
                String line = scanner.nextLine();

                // Skip empty or whitespace-only lines
                if (line.trim().isEmpty()) {
                    continue;
                }

                Task task = parseTask(line);
                if (task != null) {
                    tasks.add(task);
                } else {
                    // Log corrupted line but continue loading other tasks
                    System.out.println("     Warning: Skipping corrupted data at line " + lineNumber);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("     Error loading tasks: File not found.");
        } catch (Exception e) {
            System.out.println("     Error loading tasks: " + e.getMessage());
            System.out.println("     Some tasks may not have been loaded.");
        }

        return tasks;
    }

    /**
     * Parses a line from the data file and creates the appropriate Task object.
     * Format: TYPE | STATUS | DESCRIPTION | [ADDITIONAL_FIELDS]
     * Handles escaped characters and validates all fields.
     *
     * @param line The line to parse.
     * @return The parsed Task object, or null if the line is invalid.
     */
    private Task parseTask(String line) {
        try {
            // Split on field separator but need to handle escaped pipes
            String[] parts = line.split("(?<!\\\\) \\| ");

            if (parts.length < 3) {
                return null;
            }

            String type = parts[0].trim();
            String statusStr = parts[1].trim();

            // Validate status is either "0" or "1"
            if (!statusStr.equals(Task.STATUS_NOT_DONE) && !statusStr.equals(Task.STATUS_DONE)) {
                return null;
            }

            boolean isDone = statusStr.equals(Task.STATUS_DONE);
            String description = unescapeText(parts[2]);

            // Validate description is not empty after unescaping
            if (description.trim().isEmpty()) {
                return null;
            }

            Task task = null;

            switch (type) {
                case Todo.TASK_TYPE:
                    if (parts.length != 3) {
                        return null; // Todo should have exactly 3 parts
                    }
                    task = new Todo(description);
                    break;
                case Deadline.TASK_TYPE:
                    if (parts.length != 4) {
                        return null; // Deadline should have exactly 4 parts
                    }
                    String by = unescapeText(parts[3]);
                    if (by.trim().isEmpty()) {
                        return null;
                    }
                    try {
                        task = Deadline.createWithDateString(description, by);
                    } catch (Exception e) {
                        return null; // Invalid date format in file
                    }
                    break;
                case Event.TASK_TYPE:
                    if (parts.length != 5) {
                        return null; // Event should have exactly 5 parts
                    }
                    String from = unescapeText(parts[3]);
                    String to = unescapeText(parts[4]);
                    if (from.trim().isEmpty() || to.trim().isEmpty()) {
                        return null;
                    }
                    task = new Event(description, from, to);
                    break;
                default:
                    return null; // Unknown task type
            }

            if (task != null && isDone) {
                task.markAsDone();
            }

            return task;
        } catch (Exception e) {
            return null; // Return null for any parsing errors
        }
    }

    /**
     * Saves the task list to disk atomically.
     * Creates the data directory and file if they don't exist.
     * Uses a temporary file and atomic move to prevent data corruption.
     *
     * @param tasks The list of tasks to save.
     */
    public void saveTasks(ArrayList<Task> tasks) {
        assert tasks != null : "Task list cannot be null when saving";
        File file = new File(filePath);
        File parentDir = file.getParentFile();

        try {
            // Create data directory if it doesn't exist
            if (parentDir != null && !parentDir.exists()) {
                boolean dirCreated = parentDir.mkdirs();
                if (!dirCreated && !parentDir.exists()) {
                    System.out.println("     Error: Could not create data directory.");
                    return;
                }
            }

            // Check if directory is writable
            if (parentDir != null && !parentDir.canWrite()) {
                System.out.println("     Error: Data directory is not writable.");
                return;
            }

            // Write to temporary file first for atomic save
            File tempFile = new File(filePath + ".tmp");

            try (FileWriter writer = new FileWriter(tempFile)) {
                for (Task task : tasks) {
                    String line = formatTaskForFile(task);
                    writer.write(line + "\n");
                }
            }

            // Atomic move from temp file to actual file
            Files.move(tempFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            System.out.println("     Error saving tasks: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("     Unexpected error while saving: " + e.getMessage());
        }
    }

    /**
     * Formats a task for file storage with proper escaping.
     *
     * @param task The task to format.
     * @return The formatted string ready to be written to file.
     */
    private String formatTaskForFile(Task task) {
        String status = task.isDone() ? Task.STATUS_DONE : Task.STATUS_NOT_DONE;

        if (task instanceof Todo) {
            return Todo.TASK_TYPE + Task.FIELD_SEPARATOR + status
                    + Task.FIELD_SEPARATOR + escapeText(task.getDescription());
        } else if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return Deadline.TASK_TYPE + Task.FIELD_SEPARATOR + status
                    + Task.FIELD_SEPARATOR + escapeText(task.getDescription())
                    + Task.FIELD_SEPARATOR + escapeText(deadline.getByForStorage());
        } else if (task instanceof Event) {
            Event event = (Event) task;
            return Event.TASK_TYPE + Task.FIELD_SEPARATOR + status
                    + Task.FIELD_SEPARATOR + escapeText(task.getDescription())
                    + Task.FIELD_SEPARATOR + escapeText(event.getFrom())
                    + Task.FIELD_SEPARATOR + escapeText(event.getTo());
        } else {
            // Fallback for base Task class - should never happen in normal operation
            assert false : "Unexpected task type: " + task.getClass().getName();
            return Todo.TASK_TYPE + Task.FIELD_SEPARATOR + status
                    + Task.FIELD_SEPARATOR + escapeText(task.getDescription());
        }
    }
}
