package zikiai.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import zikiai.exception.ZikiaiException;
import zikiai.task.Deadline;
import zikiai.task.Event;
import zikiai.task.Priority;
import zikiai.task.Task;
import zikiai.task.TaskList;
import zikiai.task.Todo;

/**
 * Loads and saves the chatbot's tasks using a local data file.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates storage that reads and writes Zikiai's default data file.
     */
    public Storage() {
        this(Path.of("data", "zikiai.txt"));
    }

    /**
     * Creates storage for a chosen file, allowing tests to use temporary data.
     *
     * @param filePath location of the task file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath.toAbsolutePath();
    }

    /**
     * Writes the complete task list to the data file, replacing its old contents.
     *
     * @param tasks task list to save.
     * @throws ZikiaiException if the directory or file cannot be written.
     */
    public void save(TaskList tasks) throws ZikiaiException {
        try {
            Files.createDirectories(filePath.getParent());

            List<String> lines = new ArrayList<>();
            for (int i = 0; i < tasks.size(); i++) {
                lines.add(tasks.get(i).toDataString());
            }
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new ZikiaiException(
                    "I couldn't save your tasks to the data file.", exception);
        }
    }

    /**
     * Loads tasks from the data file. A missing file represents an empty task list.
     *
     * @return tasks reconstructed from the data file.
     * @throws ZikiaiException if the file cannot be read or contains invalid data.
     */
    public List<Task> load() throws ZikiaiException {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return tasks;
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new ZikiaiException("I couldn't read your saved tasks.", exception);
        }

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }
            tasks.add(parseTask(line, i + 1));
        }
        return tasks;
    }

    /**
     * Converts one validated data-file line into its corresponding task type.
     *
     * @param line line to parse.
     * @param lineNumber one-based line number used in error messages.
     * @return reconstructed task.
     * @throws ZikiaiException if the line does not match the storage format.
     */
    private Task parseTask(String line, int lineNumber) throws ZikiaiException {
        String[] parts = line.split("\\s*\\|\\s*", -1);
        String taskHeader = parts[0];

        if (!taskHeader.matches("\\[[TDE]\\]\\[[X ]\\](?:\\[[HML]\\])?")) {
            throw invalidDataLine(lineNumber);
        }

        char taskType = taskHeader.charAt(1);
        int expectedParts = taskType == 'T' ? 2 : taskType == 'D' ? 3 : 4;
        if (parts.length != expectedParts) {
            throw invalidDataLine(lineNumber);
        }
        for (int i = 1; i < parts.length; i++) {
            if (parts[i].isBlank()) {
                throw invalidDataLine(lineNumber);
            }
        }

        Task task;
        if (taskType == 'T') {
            task = new Todo(parts[1]);
        } else if (taskType == 'D') {
            try {
                task = new Deadline(parts[1], LocalDate.parse(parts[2]));
            } catch (DateTimeParseException exception) {
                throw invalidDataLine(lineNumber);
            }
        } else {
            task = new Event(parts[1], parts[2], parts[3]);
        }

        if (taskHeader.charAt(4) == 'X') {
            task.markAsDone();
        }
        task.setPriority(parsePriority(taskHeader, lineNumber));
        return task;
    }

    /**
     * Returns the optional priority encoded in a validated task header.
     */
    private Priority parsePriority(String taskHeader, int lineNumber) throws ZikiaiException {
        if (taskHeader.length() == 6) {
            return Priority.NONE;
        }
        return switch (taskHeader.charAt(7)) {
            case 'H' -> Priority.HIGH;
            case 'M' -> Priority.MEDIUM;
            case 'L' -> Priority.LOW;
            default -> throw invalidDataLine(lineNumber);
        };
    }

    /**
     * Creates a consistent error for malformed saved data.
     *
     * @param lineNumber one-based line number containing invalid data.
     * @return exception describing the corrupt line.
     */
    private ZikiaiException invalidDataLine(int lineNumber) {
        return new ZikiaiException(
                "I couldn't load the saved tasks because line " + lineNumber + " is invalid.");
    }
}
