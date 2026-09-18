package bobby.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;

import bobby.task.Deadline;
import bobby.task.Event;
import bobby.task.Task;
import bobby.task.Todo;

/**
 * Reads and writes the task list at its fixed location on disk.
 */
public final class Storage {
    private static final int TASK_TYPE_INDEX = 0;
    private static final int TASK_STATUS_INDEX = 1;
    private static final int TASK_DESCRIPTION_INDEX = 2;
    private static final int DEADLINE_DATE_TIME_INDEX = 3;
    private static final int EVENT_START_DATE_TIME_INDEX = 3;
    private static final int EVENT_END_DATE_TIME_INDEX = 4;
    private static final int LEGACY_TODO_FIELD_COUNT = 3;
    private static final int LEGACY_DEADLINE_FIELD_COUNT = 4;
    private static final int LEGACY_EVENT_FIELD_COUNT = 5;
    private static final int TODO_FIELD_COUNT = 4;
    private static final int DEADLINE_FIELD_COUNT = 5;
    private static final int EVENT_FIELD_COUNT = 6;
    private static final String TODO_TYPE = "T";
    private static final String DEADLINE_TYPE = "D";
    private static final String EVENT_TYPE = "E";
    private static final String INCOMPLETE_STATUS = "0";
    private static final String COMPLETE_STATUS = "1";
    private static final String UNKNOWN_COMPLETION_DATE_TIME = "-";
    private static final Path SAVE_FILE = Path.of("data", "bobby.txt");
    private static final DateTimeFormatter COMPLETION_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss")
                    .withResolverStyle(ResolverStyle.STRICT);

    private Storage() {
    }

    /**
     * Writes the complete current task list, replacing the previous saved copy.
     *
     * @param tasks the tasks to save
     * @throws IOException if the save location cannot be created or written
     */
    public static void save(List<Task> tasks) throws IOException {
        try {
            List<String> taskLines = getValidatedTaskLines(tasks);
            writeAtomically(taskLines);
        } catch (SecurityException e) {
            throw new IOException("Access to the task data was denied.", e);
        }
    }

    /**
     * Loads the saved task list, if a save file exists.
     *
     * @return the loaded tasks, or an empty list when no save file exists
     * @throws IOException if the save file cannot be read or contains an invalid task record
     */
    public static List<Task> load() throws IOException {
        try {
            return loadTasks();
        } catch (SecurityException e) {
            throw new IOException("Access to the task data was denied.", e);
        }
    }

    /**
     * Loads and validates all task records after file-access errors have been adapted.
     */
    private static List<Task> loadTasks() throws IOException {
        if (Files.notExists(SAVE_FILE)) {
            return new ArrayList<>();
        }

        List<Task> tasks = new ArrayList<>();
        for (String line : Files.readAllLines(SAVE_FILE, StandardCharsets.UTF_8)) {
            if (line.isBlank()) {
                throw new IOException("Blank task record.");
            }
            Task task = parseTask(line);
            ensureTaskIsUnique(tasks, task);
            tasks.add(task);
        }
        return tasks;
    }

    /**
     * Converts tasks to records only after confirming that every record can be loaded safely.
     */
    private static List<String> getValidatedTaskLines(List<Task> tasks) throws IOException {
        if (tasks == null) {
            throw new IOException("Missing task list.");
        }

        List<String> taskLines = new ArrayList<>();
        List<Task> validatedTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task == null) {
                throw new IOException("Task list contains a missing task.");
            }
            String taskLine = task.toFileString();
            if (taskLine.chars().anyMatch(character -> character == '\r' || character == '\n')) {
                throw new IOException("Task data contains a line break.");
            }
            Task validatedTask = parseTask(taskLine);
            ensureTaskIsUnique(validatedTasks, validatedTask);
            validatedTasks.add(validatedTask);
            taskLines.add(taskLine);
        }
        return taskLines;
    }

    /**
     * Replaces the save file without exposing a partially written task list.
     */
    private static void writeAtomically(List<String> taskLines) throws IOException {
        Path saveDirectory = SAVE_FILE.getParent();
        Files.createDirectories(saveDirectory);
        Path temporaryFile = Files.createTempFile(saveDirectory, "bobby-", ".tmp");
        IOException writeFailure = null;
        boolean isMoveComplete = false;
        try {
            Files.write(temporaryFile, taskLines, StandardCharsets.UTF_8);
            try {
                Files.move(temporaryFile, SAVE_FILE, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporaryFile, SAVE_FILE, StandardCopyOption.REPLACE_EXISTING);
            }
            isMoveComplete = true;
        } catch (IOException e) {
            writeFailure = e;
            throw e;
        } finally {
            if (!isMoveComplete) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException cleanupFailure) {
                    if (writeFailure == null) {
                        throw cleanupFailure;
                    }
                    writeFailure.addSuppressed(cleanupFailure);
                }
            }
        }
    }

    /**
     * Rejects a task whose defining details duplicate a task already read.
     */
    private static void ensureTaskIsUnique(List<Task> tasks, Task candidate) throws IOException {
        if (tasks.stream().anyMatch(task -> task.hasSameDetailsAs(candidate))) {
            throw new IOException("Duplicate task data.");
        }
    }

    /**
     * Converts one saved line into the corresponding task object.
     */
    private static Task parseTask(String line) throws IOException {
        String[] fields = getTrimmedFields(line);
        validateCommonFields(fields);

        Task task = createTask(fields);
        applyCompletionStatus(task, fields[TASK_STATUS_INDEX],
                getCompletionDateTimeValue(fields));
        return task;
    }

    /**
     * Splits a saved record into trimmed fields.
     */
    private static String[] getTrimmedFields(String line) {
        String[] fields = line.split("\\|", -1);
        for (int i = 0; i < fields.length; i++) {
            fields[i] = fields[i].trim();
        }
        return fields;
    }

    /**
     * Validates the fields shared by every saved task type.
     */
    private static void validateCommonFields(String[] fields) throws IOException {
        boolean hasRequiredFields = fields.length >= LEGACY_TODO_FIELD_COUNT;
        boolean hasTaskType = hasRequiredFields && !fields[TASK_TYPE_INDEX].isEmpty();
        boolean hasTaskStatus = hasRequiredFields && !fields[TASK_STATUS_INDEX].isEmpty();
        boolean hasDescription = hasRequiredFields && !fields[TASK_DESCRIPTION_INDEX].isEmpty();
        if (!hasTaskType || !hasTaskStatus || !hasDescription) {
            throw new IOException("Invalid task data.");
        }
    }

    /**
     * Creates the task represented by validated common fields.
     */
    private static Task createTask(String[] fields) throws IOException {
        switch (fields[TASK_TYPE_INDEX]) {
            case TODO_TYPE:
                return createTodo(fields);
            case DEADLINE_TYPE:
                return createDeadline(fields);
            case EVENT_TYPE:
                return createEvent(fields);
            default:
                throw new IOException("Unknown task type.");
        }
    }

    /**
     * Creates a to-do from a record with the required number of fields.
     */
    private static Todo createTodo(String[] fields) throws IOException {
        if (!hasSupportedFieldCount(fields, LEGACY_TODO_FIELD_COUNT, TODO_FIELD_COUNT)) {
            throw new IOException("Invalid to-do data.");
        }
        return new Todo(fields[TASK_DESCRIPTION_INDEX]);
    }

    /**
     * Creates a deadline from a record with a valid deadline field.
     */
    private static Deadline createDeadline(String[] fields) throws IOException {
        boolean hasDeadline = hasSupportedFieldCount(
                fields, LEGACY_DEADLINE_FIELD_COUNT, DEADLINE_FIELD_COUNT)
                && !fields[DEADLINE_DATE_TIME_INDEX].isEmpty();
        if (!hasDeadline) {
            throw new IOException("Invalid deadline data.");
        }
        return new Deadline(fields[TASK_DESCRIPTION_INDEX],
                parseDateTime(fields[DEADLINE_DATE_TIME_INDEX]));
    }

    /**
     * Creates an event from a record with valid start and end fields.
     */
    private static Event createEvent(String[] fields) throws IOException {
        boolean hasStartAndEnd = hasSupportedFieldCount(
                fields, LEGACY_EVENT_FIELD_COUNT, EVENT_FIELD_COUNT)
                && !fields[EVENT_START_DATE_TIME_INDEX].isEmpty()
                && !fields[EVENT_END_DATE_TIME_INDEX].isEmpty();
        if (!hasStartAndEnd) {
            throw new IOException("Invalid event data.");
        }
        LocalDateTime startDateTime = parseDateTime(fields[EVENT_START_DATE_TIME_INDEX]);
        LocalDateTime endDateTime = parseDateTime(fields[EVENT_END_DATE_TIME_INDEX]);
        if (!startDateTime.isBefore(endDateTime)) {
            throw new IOException("Event start must be before its end.");
        }
        return new Event(fields[TASK_DESCRIPTION_INDEX], startDateTime, endDateTime);
    }

    /**
     * Restores a task's saved completion status.
     */
    private static void applyCompletionStatus(Task task, String status,
            String completionDateTimeValue) throws IOException {
        switch (status) {
            case COMPLETE_STATUS:
                if (completionDateTimeValue == null
                        || completionDateTimeValue.equals(UNKNOWN_COMPLETION_DATE_TIME)) {
                    task.markAsDone();
                } else {
                    task.markAsDone(parseCompletionDateTime(completionDateTimeValue));
                }
                break;
            case INCOMPLETE_STATUS:
                if (completionDateTimeValue != null
                        && !completionDateTimeValue.equals(UNKNOWN_COMPLETION_DATE_TIME)) {
                    throw new IOException("Incomplete task has a completion date and time.");
                }
                break;
            default:
                throw new IOException("Invalid task status.");
        }
    }

    /**
     * Parses an ISO-8601 date and time saved in the task file.
     */
    private static LocalDateTime parseDateTime(String dateTime) throws IOException {
        try {
            return LocalDateTime.parse(dateTime);
        } catch (DateTimeParseException e) {
            throw new IOException("Invalid date data.", e);
        }
    }

    /**
     * Returns whether a record contains either the legacy or current number of fields.
     */
    private static boolean hasSupportedFieldCount(String[] fields, int legacyFieldCount,
            int fieldCount) {
        return fields.length == legacyFieldCount || fields.length == fieldCount;
    }

    /**
     * Returns the stored completion value, or {@code null} for a legacy record.
     */
    private static String getCompletionDateTimeValue(String[] fields) throws IOException {
        switch (fields[TASK_TYPE_INDEX]) {
            case TODO_TYPE:
                return fields.length == TODO_FIELD_COUNT ? fields[fields.length - 1] : null;
            case DEADLINE_TYPE:
                return fields.length == DEADLINE_FIELD_COUNT ? fields[fields.length - 1] : null;
            case EVENT_TYPE:
                return fields.length == EVENT_FIELD_COUNT ? fields[fields.length - 1] : null;
            default:
                throw new IOException("Unknown task type.");
        }
    }

    /**
     * Parses a completion date and time saved in Bobby's fixed on-disk format.
     */
    private static LocalDateTime parseCompletionDateTime(String dateTime) throws IOException {
        try {
            return LocalDateTime.parse(dateTime, COMPLETION_DATE_TIME_FORMAT);
        } catch (DateTimeParseException e) {
            throw new IOException("Invalid completion date data.", e);
        }
    }
}
