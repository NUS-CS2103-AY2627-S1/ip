package magnus.storage;

import java.util.List;

import magnus.task.DeadlineTask;
import magnus.task.EventTask;
import magnus.task.Task;
import magnus.task.TaskType;
import magnus.task.ToDoTask;

/**
 * Converts serialized task records into task objects.
 */
public class TaskDataParser {
    private static final int FIELD_COUNT_MINIMUM = 2;
    private static final int FIELD_COUNT_TODO = 3;
    private static final int FIELD_COUNT_DATED = 4;
    private static final int FIELD_INDEX_TASK_TYPE = 0;
    private static final int FIELD_INDEX_STATUS = 1;
    private static final int FIELD_INDEX_DESCRIPTION = 2;
    private static final int FIELD_INDEX_DATE_TIME = 3;
    private static final String STATUS_INCOMPLETE = "0";
    private static final String STATUS_COMPLETE = "1";
    private static final char EVENT_TIME_DELIMITER = '-';
    private static final char ESCAPE_CHARACTER = '\\';

    /**
     * Creates a parser for serialized task records.
     */
    public TaskDataParser() {
    }

    /**
     * Parses one serialized task record.
     *
     * @param line The serialized task record to parse.
     * @return The task represented by the record.
     * @throws IllegalArgumentException If the record is malformed or contains invalid task data.
     */
    public Task parseTask(String line) {
        List<String> taskDataFields = CsvFieldParser.parseFields(line);
        if (taskDataFields.size() < FIELD_COUNT_MINIMUM) {
            throw new IllegalArgumentException("missing task type or status");
        }

        boolean isDone = isCompleteStatus(taskDataFields.get(FIELD_INDEX_STATUS));
        Task task = createTask(taskDataFields);
        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Creates the task represented by validated serialized fields.
     *
     * @param taskDataFields The decoded task fields.
     * @return The task represented by the fields.
     * @throws IllegalArgumentException If the task type or its fields are invalid.
     */
    private Task createTask(List<String> taskDataFields) {
        TaskType taskType = TaskType.parseStorageCode(taskDataFields.get(FIELD_INDEX_TASK_TYPE));
        return switch (taskType) {
            case TODO -> {
                requireFieldCount(taskDataFields, FIELD_COUNT_TODO, "to-do");
                yield new ToDoTask(requireDescription(taskDataFields));
            }
            case DEADLINE -> {
                requireFieldCount(taskDataFields, FIELD_COUNT_DATED, "deadline");
                yield new DeadlineTask(
                        requireDescription(taskDataFields),
                        requireNonBlank(taskDataFields.get(FIELD_INDEX_DATE_TIME), "deadline"));
            }
            case EVENT -> {
                requireFieldCount(taskDataFields, FIELD_COUNT_DATED, "event");
                EventTimes eventTimes = parseEventTimes(
                        requireNonBlank(taskDataFields.get(FIELD_INDEX_DATE_TIME), "event time"));
                yield new EventTask(
                        requireDescription(taskDataFields),
                        eventTimes.start(), eventTimes.end());
            }
            default -> throw new AssertionError("Unexpected task type: " + taskType);
        };
    }

    /**
     * Returns the validated description field from serialized task data.
     *
     * @param taskDataFields The decoded task fields.
     * @return The non-blank task description.
     * @throws IllegalArgumentException If the description is blank.
     */
    private String requireDescription(List<String> taskDataFields) {
        return requireNonBlank(taskDataFields.get(FIELD_INDEX_DESCRIPTION), "description");
    }

    /**
     * Returns whether a stored numeric completion status represents a completed task.
     *
     * @param status The stored status, which must be {@code "0"} or {@code "1"}.
     * @return {@code true} for a completed task; {@code false} for an incomplete task.
     * @throws IllegalArgumentException If the status is not supported.
     */
    private boolean isCompleteStatus(String status) {
        if (status.equals(STATUS_COMPLETE)) {
            return true;
        }
        if (status.equals(STATUS_INCOMPLETE)) {
            return false;
        }
        throw new IllegalArgumentException("invalid task status '" + status + "'");
    }

    /**
     * Verifies that a serialized record contains the expected number of fields.
     *
     * @param fields The parsed fields to validate.
     * @param expectedCount The required number of fields.
     * @param taskType The task type used in an error message.
     * @throws IllegalArgumentException If the number of fields is incorrect.
     */
    private void requireFieldCount(List<String> fields, int expectedCount, String taskType) {
        if (fields.size() != expectedCount) {
            throw new IllegalArgumentException(
                    taskType + " task should have " + expectedCount + " fields, but has " + fields.size());
        }
    }

    /**
     * Verifies that a required serialized field contains non-whitespace text.
     *
     * @param field The field to validate.
     * @param fieldName The field name used in an error message.
     * @return The validated field.
     * @throws IllegalArgumentException If the field is blank.
     */
    private String requireNonBlank(String field, String fieldName) {
        if (field.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
        return field;
    }

    /**
     * Separates a stored event-time field into its start and end values.
     * Backslashes escape delimiter characters within either value.
     *
     * @param eventTimeData The serialized event-time field.
     * @return The decoded start and end values.
     * @throws IllegalArgumentException If the field is malformed or either value is blank.
     */
    private EventTimes parseEventTimes(String eventTimeData) {
        StringBuilder currentPart = new StringBuilder();
        String start = null;
        boolean isEscaped = false;

        for (int i = 0; i < eventTimeData.length(); i++) {
            char currentCharacter = eventTimeData.charAt(i);
            if (isEscaped) {
                currentPart.append(currentCharacter);
                isEscaped = false;
            } else if (currentCharacter == ESCAPE_CHARACTER) {
                isEscaped = true;
            } else if (currentCharacter == EVENT_TIME_DELIMITER && start == null) {
                start = currentPart.toString();
                currentPart.setLength(0);
            } else {
                currentPart.append(currentCharacter);
            }
        }

        if (isEscaped) {
            throw new IllegalArgumentException("event time ends with an incomplete escape sequence");
        }

        String end = currentPart.toString();
        if (start == null || start.isBlank() || end.isBlank()) {
            throw new IllegalArgumentException("event time must contain a non-blank start and end");
        }
        return new EventTimes(start, end);
    }

    /**
     * Contains the start and end values decoded from a serialized event-time field.
     *
     * @param start The serialized event start time.
     * @param end The serialized event end time.
     */
    private record EventTimes(String start, String end) {
    }
}
