package panda.storage;

import panda.exception.parser.IllegalDateTimeException;
import panda.exception.storage.FileCorruptedException;
import panda.exception.task.InvalidEventParametersException;
import panda.task.Deadline;
import panda.task.Event;
import panda.task.Task;
import panda.task.Todo;
import panda.util.datetime.DateTimeHelper;

/**
 * Converts tasks to and from Panda's line-based save format.
 */
public final class TaskCodec {
    private static final int FIELD_COUNT_DEADLINE = 4;
    private static final int FIELD_COUNT_EVENT = 5;
    private static final int FIELD_COUNT_TODO = 3;
    private static final String FIELD_DELIMITER = " | ";
    private static final String FIELD_DELIMITER_REGEX = "\\|";
    private static final int FIELD_INDEX_DESCRIPTION = 2;
    private static final int FIELD_INDEX_EVENT_END_DATE = 4;
    private static final int FIELD_INDEX_PRIMARY_DATE = 3;
    private static final int FIELD_INDEX_STATE = 1;
    private static final int FIELD_INDEX_TYPE = 0;
    private static final int FIELD_SPLIT_LIMIT = -1;
    private static final String TASK_STATE_MARKED = "1";
    private static final String TASK_STATE_UNMARKED = "0";
    private static final String TASK_TYPE_DEADLINE = "D";
    private static final String TASK_TYPE_EVENT = "E";
    private static final String TASK_TYPE_TODO = "T";

    private TaskCodec() {
        // Utility class: prevent accidental instantiation.
    }

    /**
     * Encodes one task as a line suitable for the save file.
     *
     * @param task Task to encode.
     * @return Serialized task line.
     * @throws IllegalArgumentException If the task type is unsupported.
     */
    public static String encode(Task task) {
        String state = encodeTaskState(task);
        return encodeTaskByType(task, state);
    }

    /**
     * Converts a task's completion state to its storage marker.
     *
     * @param task Task whose state should be encoded.
     * @return Encoded completion-state marker.
     */
    private static String encodeTaskState(Task task) {
        return task.isMarked() ? TASK_STATE_MARKED : TASK_STATE_UNMARKED;
    }

    /**
     * Delegates task encoding to the encoder for its concrete type.
     *
     * @param task Task to encode.
     * @param state Encoded completion-state marker.
     * @return Serialized task line.
     * @throws IllegalArgumentException If the task type is unsupported.
     */
    private static String encodeTaskByType(Task task, String state) {
        if (task instanceof Todo todo) {
            return encodeTodo(todo, state);
        }
        if (task instanceof Deadline deadline) {
            return encodeDeadline(deadline, state);
        }
        if (task instanceof Event event) {
            return encodeEvent(event, state);
        }
        throw new IllegalArgumentException("Unsupported task type: " + task.getClass().getName());
    }

    /**
     * Encodes a to-do task using the to-do record layout.
     *
     * @param todo To-do task to encode.
     * @param state Encoded completion-state marker.
     * @return Serialized to-do record.
     */
    private static String encodeTodo(Todo todo, String state) {
        return String.join(FIELD_DELIMITER, TASK_TYPE_TODO, state, todo.getDescription());
    }

    /**
     * Encodes a deadline task using the deadline record layout.
     *
     * @param deadline Deadline task to encode.
     * @param state Encoded completion-state marker.
     * @return Serialized deadline record.
     */
    private static String encodeDeadline(Deadline deadline, String state) {
        return String.join(FIELD_DELIMITER, TASK_TYPE_DEADLINE, state, deadline.getDescription(),
                DateTimeHelper.formatForStorage(deadline.getDueDate()));
    }

    /**
     * Encodes an event task using the event record layout.
     *
     * @param event Event task to encode.
     * @param state Encoded completion-state marker.
     * @return Serialized event record.
     */
    private static String encodeEvent(Event event, String state) {
        return String.join(FIELD_DELIMITER, TASK_TYPE_EVENT, state, event.getDescription(),
                DateTimeHelper.formatForStorage(event.getStartDateTime()),
                DateTimeHelper.formatForStorage(event.getEndDateTime()));
    }

    /**
     * Decodes one save-file line into its corresponding task type.
     *
     * @param line Serialized task line.
     * @return Decoded task.
     * @throws FileCorruptedException If the line has an invalid format.
     */
    public static Task decode(String line) throws FileCorruptedException {
        String[] fields = parseFields(line);
        String taskType = validateRecordSchema(fields);
        Task task = decodeTask(taskType, fields);
        restoreTaskState(task, fields);
        return task;
    }

    /**
     * Splits a serialized task record into non-empty, normalized fields.
     *
     * @param line Serialized task line.
     * @return Normalized record fields.
     * @throws FileCorruptedException If the line is null or contains an empty field.
     */
    private static String[] parseFields(String line) throws FileCorruptedException {
        if (line == null) {
            throw new FileCorruptedException("Task record cannot be null");
        }

        String[] fields = line.split(FIELD_DELIMITER_REGEX, FIELD_SPLIT_LIMIT);
        for (int index = 0; index < fields.length; index++) {
            fields[index] = fields[index].strip();
            if (fields[index].isEmpty()) {
                throw new FileCorruptedException("Missing data");
            }
        }
        return fields;
    }

    /**
     * Validates the task type and number of fields in a task record.
     *
     * @param fields Normalized record fields.
     * @return Validated task type marker.
     * @throws FileCorruptedException If the task type or field count is invalid.
     */
    private static String validateRecordSchema(String[] fields) throws FileCorruptedException {
        String taskType = fields[FIELD_INDEX_TYPE];
        int expectedFieldCount = switch (taskType) {
            case TASK_TYPE_TODO -> FIELD_COUNT_TODO;
            case TASK_TYPE_DEADLINE -> FIELD_COUNT_DEADLINE;
            case TASK_TYPE_EVENT -> FIELD_COUNT_EVENT;
            default -> throw new FileCorruptedException("Unknown task type: " + taskType);
        };
        if (fields.length != expectedFieldCount) {
            throw new FileCorruptedException(
                    "Expected " + expectedFieldCount + " fields but found " + fields.length);
        }
        return taskType;
    }

    /**
     * Creates the task represented by validated record fields.
     *
     * @param taskType Validated task type marker.
     * @param fields Validated record fields.
     * @return Decoded task.
     * @throws FileCorruptedException If a stored date cannot be parsed.
     */
    private static Task decodeTask(String taskType, String[] fields) throws FileCorruptedException {
        try {
            return switch (taskType) {
                case TASK_TYPE_TODO -> new Todo(fields[FIELD_INDEX_DESCRIPTION]);
                case TASK_TYPE_DEADLINE -> new Deadline(
                        fields[FIELD_INDEX_DESCRIPTION],
                        DateTimeHelper.parseStorage(fields[FIELD_INDEX_PRIMARY_DATE]));
                case TASK_TYPE_EVENT -> new Event(
                        fields[FIELD_INDEX_DESCRIPTION],
                        DateTimeHelper.parseStorage(fields[FIELD_INDEX_PRIMARY_DATE]),
                        DateTimeHelper.parseStorage(fields[FIELD_INDEX_EVENT_END_DATE]));
                default -> throw new AssertionError("Task type was validated before decoding");
            };
        } catch (IllegalDateTimeException exception) {
            throw new FileCorruptedException("Stored task contains an illegal date or time", exception);
        } catch (InvalidEventParametersException exception) {
            throw new FileCorruptedException("Stored event contains an invalid time range", exception);
        }
    }

    /**
     * Restores the completion state stored in a task record.
     *
     * @param task Task whose state should be restored.
     * @param fields Validated record fields.
     * @throws FileCorruptedException If the stored state is invalid.
     */
    private static void restoreTaskState(Task task, String[] fields) throws FileCorruptedException {
        if (decodeState(fields[FIELD_INDEX_STATE])) {
            task.mark();
        }
    }

    /**
     * Validates the completion-state field and converts it to a boolean.
     *
     * @param state State flag string representing whether the task is marked.
     * @return Whether the task is marked.
     * @throws FileCorruptedException If the state value is neither "0" nor "1".
     */
    private static boolean decodeState(String state) throws FileCorruptedException {
        return switch (state) {
            case TASK_STATE_MARKED -> true;
            case TASK_STATE_UNMARKED -> false;
            default -> throw new FileCorruptedException("Task state must be 0 or 1");
        };
    }
}
