package loki.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import loki.exception.LokiExceptions;
import loki.model.Deadline;
import loki.model.Event;
import loki.model.Task;
import loki.model.TaskList;
import loki.model.ToDo;
import loki.parser.DateTimeParser;

/**
 * Handles loading tasks from and saving tasks to the application's data file.
 */
public class Storage {
    private static final Path DEFAULT_TASK_FILE = Path.of("src/data/tasks.txt");
    private final Path taskFile;

    /**
     * Creates storage backed by a custom file path.
     *
     * @param filePath the path of the task data file
     * @throws IllegalArgumentException if the path is null, blank, or invalid
     */
    public Storage(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("Storage path cannot be empty");
        }
        try {
            this.taskFile = Path.of(filePath);
        } catch (InvalidPathException exception) {
            throw new IllegalArgumentException("Invalid storage path", exception);
        }
    }

    /** Creates storage backed by the application's default task file. */
    public Storage() {
        this.taskFile = DEFAULT_TASK_FILE;
    }


    /**
     * Parses one serialized task record into a task object.
     *
     * @param rawTask the pipe-delimited task record
     * @return the task represented by the record
     * @throws LokiExceptions if the record is malformed
     */
    private Task parseTaskRecord(String rawTask) throws LokiExceptions {
        if (rawTask == null || rawTask.isBlank()) {
            throw new LokiExceptions("Task record cannot be empty");
        }

        String[] fields = rawTask.split("\\s*\\|\\s*", -1);

        if (fields.length < 3) {
            throw new LokiExceptions("Invalid task format");
        }

        String type = fields[0].trim();
        int done = parseStatus(fields[1].trim());
        String title = fields[2].trim();

        if (title.isEmpty()) {
            throw new LokiExceptions("Task title cannot be empty");
        }

        return switch (type) {
            case "T" -> parseToDoRecord(fields, title, done);
            case "D" -> parseDeadlineRecord(fields, title, done);
            case "E" -> parseEventRecord(fields, title, done);
            default -> throw new LokiExceptions("Unknown Task type; Is your file corrupted?");
        };
    }

    /**
     * Parses a to-do record after validating its shared fields.
     *
     * @param fields the split record fields.
     * @param title the validated task title.
     * @param done the validated completion status.
     * @return the stored to-do task.
     * @throws LokiExceptions if the record has an unexpected field count.
     */
    private Task parseToDoRecord(String[] fields, String title, int done) throws LokiExceptions {
        requireFieldCount(fields, 3);
        return new ToDo(title, done);
    }

    /**
     * Parses a deadline record after validating its shared fields.
     *
     * @param fields the split record fields.
     * @param title the validated task title.
     * @param done the validated completion status.
     * @return the stored deadline task.
     * @throws LokiExceptions if the field count or due date is invalid.
     */
    private Task parseDeadlineRecord(String[] fields, String title, int done) throws LokiExceptions {
        requireFieldCount(fields, 4);
        String due = fields[3].trim();
        if (due.isEmpty()) {
            throw new LokiExceptions("Deadline cannot be empty");
        }
        try {
            return new Deadline(title, done, DateTimeParser.parseStored(due));
        } catch (IllegalArgumentException exception) {
            throw new LokiExceptions("Invalid deadline date/time");
        }
    }

    /**
     * Parses an event record after validating its shared fields.
     *
     * @param fields the split record fields.
     * @param title the validated task title.
     * @param done the validated completion status.
     * @return the stored event task.
     * @throws LokiExceptions if the field count or event interval is invalid.
     */
    private Task parseEventRecord(String[] fields, String title, int done) throws LokiExceptions {
        requireFieldCount(fields, 4);
        String schedule = fields[3].trim();
        int separator = schedule.indexOf("->");
        if (separator <= 0 || separator >= schedule.length() - 2) {
            throw new LokiExceptions("Invalid event time format");
        }
        String from = schedule.substring(0, separator).trim();
        String to = schedule.substring(separator + 2).trim();
        if (from.isEmpty() || to.isEmpty()) {
            throw new LokiExceptions("Invalid event time format");
        }
        try {
            LocalDateTime start = DateTimeParser.parseStored(from);
            LocalDateTime end = DateTimeParser.parseStored(to);
            return new Event(title, done, start, end);
        } catch (IllegalArgumentException exception) {
            throw new LokiExceptions("Invalid event date/time");
        }
    }

    /**
     * Parses the numeric completion status stored for a task.
     *
     * @param status the stored status text
     * @return the status as an integer
     * @throws LokiExceptions if the status is not {@code 0} or {@code 1}
     */
    private int parseStatus(String status) throws LokiExceptions {
        if (status.equals("0") || status.equals("1")) {
            return Integer.parseInt(status);
        }
        throw new LokiExceptions("Invalid task status");
    }

    /**
     * Ensures that a serialized record has the expected number of fields.
     *
     * @param fields the fields parsed from a task record
     * @param expected the required number of fields
     * @throws LokiExceptions if the number of fields is incorrect
     */
    private void requireFieldCount(String[] fields, int expected)
            throws LokiExceptions {
        if (fields.length != expected) {
            throw new LokiExceptions("Invalid task format");
        }
    }


    /**
     * Loads all non-blank task records from the backing file.
     *
     * @return the loaded tasks, or an empty list if the file does not exist
     * @throws LokiExceptions if the file cannot be read or contains an invalid record
     */
    public TaskList load() throws LokiExceptions {
        TaskList taskList = new TaskList();
        List<String> allTasks;
        try {
            allTasks = Files.readAllLines(taskFile, StandardCharsets.UTF_8);
        } catch (NoSuchFileException exception) {
            return taskList;
        } catch (IOException | SecurityException exception) {
            throw new LokiExceptions("Invalid File Path");
        }

        for (String rawTask : allTasks) {
            if (!rawTask.isBlank()) {
                taskList.add(parseTaskRecord(rawTask));
            }
        }

        return taskList;
    }

    /**
     * Serializes and writes the supplied tasks to the backing file.
     *
     * @param taskList the tasks to save
     * @throws LokiExceptions if the task list or any serialized record is invalid,
     *         or if the file cannot be written
     */
    public void save(TaskList taskList) throws LokiExceptions {
        if (taskList == null) {
            throw new LokiExceptions("Task list cannot be null");
        }

        StringBuilder tasks = new StringBuilder();
        for (Task task : taskList) {
            if (task == null) {
                throw new LokiExceptions("Task list cannot contain null tasks");
            }
            String serializedTask = task.saveString();
            if (serializedTask == null || serializedTask.isBlank()
                    || serializedTask.indexOf('\n') >= 0 || serializedTask.indexOf('\r') >= 0) {
                throw new LokiExceptions("Task has an invalid storage format");
            }
            tasks.append(serializedTask);
            tasks.append("\n");
        }
        try {
            Path parent = taskFile.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(taskFile, tasks.toString(), StandardCharsets.UTF_8);
        } catch (IOException | SecurityException exception) {
            throw new LokiExceptions("Unable to save file");
        }

    }
}
