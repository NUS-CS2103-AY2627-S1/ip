package bubu.storage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import bubu.task.Deadline;
import bubu.task.Event;
import bubu.task.Task;
import bubu.task.ToDo;
import bubu.util.DateTimeParser;

/**
 * Saves the current task list in a simple text file in the project data folder.
 */
public class Storage {
    private static final Path DEFAULT_FILE_PATH = Path.of("data", "bubu.txt");
    private static final String DELIMITER_WRITE = " | ";
    private static final String REGEX_DELIMITER_READ = " \\| ";

    private static final String TYPE_TODO = "T";
    private static final String TYPE_DEADLINE = "D";
    private static final String TYPE_EVENT = "E";

    private static final String STATUS_DONE = "1";
    private static final String STATUS_NOT_DONE = "0";

    private static final String SAVE_ERROR_MESSAGE = "Unable to save tasks to %s";
    private static final String LOAD_ERROR_MESSAGE = "Unable to read tasks from %s";
    private static final String UNSUPPORTED_TASK_MESSAGE = "Unsupported task type: %s. Meow!";
    private static final String UNKNOWN_TYPE_MESSAGE = "Unknown type: %s";
    private static final String UNKNOWN_TASK_TYPE_MESSAGE = "Unknown task type: %s";
    private static final String MALFORMED_RECORD_MESSAGE = "Malformed task record: %s";
    private static final String INVALID_EVENT_RANGE_MESSAGE = "Invalid event range in stored task";

    private static final int TASK_TYPE_INDEX = 0;
    private static final int TASK_STATUS_INDEX = 1;
    private static final int TASK_DESCRIPTION_INDEX = 2;
    private static final int DEADLINE_TIME_INDEX = 3;
    private static final int EVENT_START_INDEX = 3;
    private static final int EVENT_END_INDEX = 4;
    private static final int MINIMUM_TASK_PARTS = 3;
    private static final int TODO_PART_COUNT = 3;
    private static final int DEADLINE_PART_COUNT = 4;
    private static final int EVENT_PART_COUNT = 5;

    private static final LocalTime DEFAULT_END_TIME = LocalTime.of(23, 59);
    private static final LocalTime DEFAULT_START_TIME = LocalTime.MIDNIGHT;
    private final Path filePath;

    /** Creates storage using the application's default task file. */
    public Storage() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Creates storage using a specific task file.
     *
     * @param filePath file used to save and load tasks.
     */
    public Storage(Path filePath) {
        assert filePath != null : "Storage file path cannot be null";
        this.filePath = filePath;
    }

    /**
     * Replaces the saved task file with the current contents of the task list.
     *
     * @param tasks Tasks to save.
     */
    public void saveTasks(List<Task> tasks) {
        assert tasks != null : "Task list should not be null when saving to file.";
        List<String> lines = tasks.stream()
                .map(this::format)
                .toList();
        try {
            Path parentDirectory = filePath.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }
            Files.write(filePath, lines);
        } catch (IOException e) {
            throw new UncheckedIOException(String.format(SAVE_ERROR_MESSAGE, filePath), e);
        }
    }

    /**
     * Converts one task to its serialized file representation.
     *
     * @param task Task to format.
     * @return Single line representing task details.
     */
    private String format(Task task) {
        String status = task.isDone() ? STATUS_DONE : STATUS_NOT_DONE;

        if (task instanceof ToDo) {
            return String.join(DELIMITER_WRITE, TYPE_TODO, status, task.getDescription());
        }
        if (task instanceof Deadline deadline) {
            return String.join(DELIMITER_WRITE,
                    TYPE_DEADLINE,
                    status,
                    deadline.getDescription(),
                    DateTimeParser.format(deadline.getDeadline()));
        }
        if (task instanceof Event event) {
            return String.join(DELIMITER_WRITE,
                    TYPE_EVENT,
                    status,
                    event.getDescription(),
                    DateTimeParser.format(event.getStart()),
                    DateTimeParser.format(event.getEnd()));
        }

        throw new IllegalArgumentException(String.format(UNSUPPORTED_TASK_MESSAGE,
                task.getClass().getName()));
    }

    /**
     * Loads tasks from the save file into memory.
     *
     * @return List of loaded tasks, or an empty list if the file doesn't exist yet.
     */
    public List<Task> load() {
        List<Task> loadedTasks = new ArrayList<>();

        if (!Files.exists(filePath)) {
            return loadedTasks;
        }

        try {
            List<String> lines = Files.readAllLines(filePath);
            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                loadedTasks.add(parseTask(line));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(String.format(LOAD_ERROR_MESSAGE, filePath), e);
        }

        assert loadedTasks != null : "Loaded tasks list should not be null";
        return loadedTasks;
    }

    /**
     * Parses a line from the save file into a Task object.
     *
     * @param line Line from the save file.
     * @return Parsed task object.
     * @throws IllegalArgumentException If the line format is invalid or task type is unknown.
     */
    private Task parseTask(String line) {
        String[] parts = line.split(REGEX_DELIMITER_READ);
        validateStoredTaskParts(parts, line);
        String taskType = parts[TASK_TYPE_INDEX];

        Task task = switch (taskType) {
            case TYPE_TODO -> new ToDo(parts[TASK_DESCRIPTION_INDEX]);
            case TYPE_DEADLINE -> new Deadline(parts[TASK_DESCRIPTION_INDEX],
                    DateTimeParser.parseStored(parts[DEADLINE_TIME_INDEX], DEFAULT_END_TIME));
            case TYPE_EVENT -> parseStoredEvent(parts);
            default -> throw new IllegalArgumentException(String.format(UNKNOWN_TYPE_MESSAGE, taskType));
        };

        if (parts[TASK_STATUS_INDEX].equals(STATUS_DONE)) {
            task.markAsDone();
        }

        return task;
    }

    /**
     * Parses an event record and validates its chronological range.
     *
     * @param parts fields from a stored event record.
     * @return parsed event.
     * @throws IllegalArgumentException if the event range is invalid.
     */
    private Event parseStoredEvent(String[] parts) {
        LocalDateTime start = DateTimeParser.parseStored(parts[EVENT_START_INDEX], DEFAULT_START_TIME);
        LocalDateTime end = DateTimeParser.parseStored(parts[EVENT_END_INDEX], DEFAULT_END_TIME);
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException(INVALID_EVENT_RANGE_MESSAGE);
        }
        return new Event(parts[TASK_DESCRIPTION_INDEX], start, end);
    }

    /**
     * Validates the common fields of a stored task record before parsing it.
     *
     * @param parts fields extracted from a stored record.
     * @param line original record, used in the error message.
     * @throws IllegalArgumentException if the record is malformed.
     */
    private void validateStoredTaskParts(String[] parts, String line) {
        if (parts.length < MINIMUM_TASK_PARTS
                || (!parts[TASK_STATUS_INDEX].equals(STATUS_DONE)
                && !parts[TASK_STATUS_INDEX].equals(STATUS_NOT_DONE))) {
            throw new IllegalArgumentException(String.format(MALFORMED_RECORD_MESSAGE, line));
        }

        String taskType = parts[TASK_TYPE_INDEX];
        int expectedPartCount = switch (taskType) {
            case TYPE_TODO -> TODO_PART_COUNT;
            case TYPE_DEADLINE -> DEADLINE_PART_COUNT;
            case TYPE_EVENT -> EVENT_PART_COUNT;
            default -> throw new IllegalArgumentException(
                    String.format(UNKNOWN_TASK_TYPE_MESSAGE, taskType));
        };
        if (parts.length != expectedPartCount
                || parts[TASK_DESCRIPTION_INDEX].trim().isEmpty()) {
            throw new IllegalArgumentException(String.format(MALFORMED_RECORD_MESSAGE, line));
        }
    }

}
