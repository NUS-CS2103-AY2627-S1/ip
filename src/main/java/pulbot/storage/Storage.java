package pulbot.storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

import pulbot.PulbotException;
import pulbot.task.Deadline;
import pulbot.task.Event;
import pulbot.task.Task;
import pulbot.task.TaskList;
import pulbot.task.TaskType;
import pulbot.task.Todo;

/** Handles loading tasks from and saving tasks to a file. */
public class Storage {
    private static final int TASK_TYPE_COLUMN = 0;
    private static final int MARK_STATUS_COLUMN = 1;
    private static final int DESCRIPTION_COLUMN = 2;
    private static final int FIRST_DATE_TIME_COLUMN = 3;
    private static final int SECOND_DATE_TIME_COLUMN = 4;
    private static final int TODO_COLUMN_COUNT = 3;
    private static final int DEADLINE_COLUMN_COUNT = 4;
    private static final int EVENT_COLUMN_COUNT = 5;
    private static final String NOT_MARKED_STATUS = "0";
    private static final String MARKED_STATUS = "1";
    private static final DateTimeFormatter STORED_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd uuuu h:mm a", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT);
    private final Path filePath;
    /** Prevents an empty recovery session from overwriting data that could not be loaded. */
    private boolean isSaveBlocked;

    /** Creates storage backed by the supplied file path. */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /** Loads tasks from disk, returning an empty list when the file is absent. */
    public TaskList load() throws PulbotException {
        TaskList tasks = new TaskList();
        if (Files.notExists(filePath)) {
            isSaveBlocked = false;
            return tasks;
        }
        isSaveBlocked = true;
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                tasks.add(parseTask(line));
            }
        } catch (IllegalArgumentException e) {
            throw new PulbotException(e.getMessage());
        } catch (IOException e) {
            throw new PulbotException("Error reading file: " + e.getMessage());
        }
        isSaveBlocked = false;
        return tasks;
    }

    /** Saves all tasks to disk in the application file format. */
    public void save(TaskList tasks) throws PulbotException {
        assert tasks != null : "Storage can only save an initialized task list";
        if (isSaveBlocked) {
            throw new PulbotException("Saved data could not be loaded. Repair or move the data file, "
                    + "then restart Pulbot before changing tasks. The original file has been preserved.");
        }
        try {
            createParentDirectory();
            Files.writeString(filePath, serializeTasks(tasks));
        } catch (IOException e) {
            throw new PulbotException("Error writing to file. Your task change was not saved: " + e.getMessage());
        }
    }

    private void createParentDirectory() throws IOException {
        Path parentDirectory = filePath.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }
    }

    private String serializeTasks(TaskList tasks) {
        StringBuilder contents = new StringBuilder();
        for (Task task : tasks) {
            contents.append(serializeTask(task)).append(System.lineSeparator());
        }
        return contents.toString();
    }

    /** Converts one task to the tab-separated storage format. */
    private String serializeTask(Task task) {
        StringBuilder taskData = new StringBuilder();
        taskData.append(task.getType().getSymbol())
                .append('\t')
                .append(task.isDone() ? MARKED_STATUS : NOT_MARKED_STATUS)
                .append('\t')
                .append(task.getDescription());
        if (task instanceof Deadline deadline) {
            taskData.append('\t').append(formatDateTime(deadline.getBy()));
        } else if (task instanceof Event event) {
            taskData.append('\t').append(formatDateTime(event.getFrom()))
                    .append('\t').append(formatDateTime(event.getTo()));
        }
        return taskData.toString();
    }

    private LocalDateTime parseStoredDateTime(String value) throws IllegalArgumentException {
        try {
            return LocalDateTime.parse(value.trim(), STORED_DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date in task file: " + value);
        }
    }

    private String formatDateTime(LocalDateTime value) {
        return value.format(STORED_DATE_FORMATTER);
    }

    /** Converts one validated storage line into a task. */
    private Task parseTask(String line) throws PulbotException {
        String[] columns = line.split("\t", -1);
        TaskType taskType = parseTaskType(columns[TASK_TYPE_COLUMN]);
        validateColumns(line, columns, taskType);

        Task task = createTask(columns, taskType);
        if (columns[MARK_STATUS_COLUMN].equals(MARKED_STATUS)) {
            task.markAsDone();
        }
        return task;
    }

    /** Checks that a storage line contains valid common and type-specific fields. */
    private void validateColumns(String line, String[] columns, TaskType taskType)
            throws PulbotException {
        int expectedColumns = getExpectedColumnCount(taskType);
        if (columns.length != expectedColumns) {
            throw new PulbotException("Invalid line format in file: " + line);
        }

        String markStatus = columns[MARK_STATUS_COLUMN];
        if (!markStatus.equals(NOT_MARKED_STATUS) && !markStatus.equals(MARKED_STATUS)) {
            throw new PulbotException("Invalid mark status in file: " + markStatus);
        }
        if (columns[DESCRIPTION_COLUMN].isBlank()) {
            throw new PulbotException("Task description cannot be empty.");
        }
        validateDateTimeColumns(columns, taskType);
    }

    /** Creates the task represented by validated storage columns. */
    private Task createTask(String[] columns, TaskType taskType) {
        String description = columns[DESCRIPTION_COLUMN];
        switch (taskType) {
            case TODO:
                return new Todo(description);
            case DEADLINE:
                return new Deadline(description,
                        parseStoredDateTime(columns[FIRST_DATE_TIME_COLUMN]));
            case EVENT:
                return new Event(description,
                        parseStoredDateTime(columns[FIRST_DATE_TIME_COLUMN]),
                        parseStoredDateTime(columns[SECOND_DATE_TIME_COLUMN]));
            default:
                throw new IllegalArgumentException("Unsupported task type: " + taskType);
        }
    }

    /** Maps a stored task-type code to its enum value. */
    private TaskType parseTaskType(String typeCode) throws PulbotException {
        for (TaskType taskType : TaskType.values()) {
            if (taskType.getSymbol().equals(typeCode)) {
                return taskType;
            }
        }
        throw new PulbotException("Invalid task type in file: " + typeCode);
    }

    /** Returns the required storage column count for a task type. */
    private int getExpectedColumnCount(TaskType taskType) {
        switch (taskType) {
            case TODO:
                return TODO_COLUMN_COUNT;
            case DEADLINE:
                return DEADLINE_COLUMN_COUNT;
            case EVENT:
                return EVENT_COLUMN_COUNT;
            default:
                throw new IllegalArgumentException("Unsupported task type: " + taskType);
        }
    }

    /** Checks that the date-time columns required by a task type are present. */
    private void validateDateTimeColumns(String[] columns, TaskType taskType)
            throws PulbotException {
        switch (taskType) {
            case TODO:
                return;
            case DEADLINE:
                if (columns[FIRST_DATE_TIME_COLUMN].isBlank()) {
                    throw new PulbotException("Deadline date cannot be empty.");
                }
                return;
            case EVENT:
                boolean hasStartTime = !columns[FIRST_DATE_TIME_COLUMN].isBlank();
                boolean hasEndTime = !columns[SECOND_DATE_TIME_COLUMN].isBlank();
                if (!hasStartTime || !hasEndTime) {
                    throw new PulbotException("Event start and end times cannot be empty.");
                }
                return;
            default:
                throw new IllegalArgumentException("Unsupported task type: " + taskType);
        }
    }
}
