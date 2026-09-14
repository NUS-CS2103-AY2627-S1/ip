package penguin.parser;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

import penguin.enums.TaskType;
import penguin.exception.PenguinException;
import penguin.task.Deadline;
import penguin.task.Event;
import penguin.task.Task;
import penguin.task.Todo;
import penguin.util.DateTimeUtil;

/**
 * Converts persisted task records into validated task objects.
 */
public final class SavedTaskParser {
    /**
     * Prevents instantiation of this parser utility class.
     */
    private SavedTaskParser() {
    }

    /**
     * Converts one saved line into a task.
     *
     * @param line saved task data.
     * @return task represented by the line.
     * @throws PenguinException if the line is malformed.
     */
    public static Task parse(String line) throws PenguinException {
        if (line == null || line.isBlank()) {
            throw new PenguinException("Invalid task data. File may be corrupted.");
        }
        String[] parts = line.split("\\s*" + Pattern.quote(Task.STORAGE_DELIMITER)
                + "\\s*", -1);
        if (parts.length < 3 || parts[2].isBlank()) {
            throw new PenguinException("Invalid task data. File may be corrupted.");
        }
        if (!parts[1].equals("0") && !parts[1].equals("1")) {
            throw new PenguinException("Invalid task status. File may be corrupted.");
        }

        Task task = createTask(parts[0], parts);
        if (parts[1].equals("1")) {
            task.markDone();
        }
        return task;
    }

    /**
     * Creates a task according to its persisted type and fields.
     *
     * @param type persisted task type symbol.
     * @param parts fields from the persisted record.
     * @return reconstructed task.
     * @throws PenguinException if the type or fields are invalid.
     */
    private static Task createTask(String type, String[] parts) throws PenguinException {
        TaskType taskType = TaskType.fromSymbol(type);
        if (taskType == null) {
            throw new PenguinException("Unknown task type.");
        }
        String description = parts[2];
        return switch (taskType) {
            case TODO -> parseTodo(parts, description);
            case DEADLINE -> parseDeadline(parts, description);
            case EVENT -> parseEvent(parts, description);
            default -> throw new PenguinException("Unknown task type.");
        };
    }

    /**
     * Parses a persisted to-do record.
     *
     * @param parts fields from the persisted record.
     * @param description persisted task description.
     * @return reconstructed to-do task.
     * @throws PenguinException if the record has an invalid number of fields.
     */
    private static Task parseTodo(String[] parts, String description) throws PenguinException {
        if (parts.length != 3) {
            throw new PenguinException("Invalid todo data.");
        }
        return new Todo(description);
    }

    /**
     * Parses a persisted deadline record.
     *
     * @param parts fields from the persisted record.
     * @param description persisted task description.
     * @return reconstructed deadline task.
     * @throws PenguinException if the record or date/time is invalid.
     */
    private static Task parseDeadline(String[] parts, String description)
            throws PenguinException {
        if (parts.length != 4 || parts[3].isBlank()) {
            throw new PenguinException("Invalid deadline data.");
        }
        LocalDateTime dateTime = DateTimeUtil.parseDateTime(parts[3]);
        return new Deadline(description, dateTime);
    }

    /**
     * Parses a persisted event record.
     *
     * @param parts fields from the persisted record.
     * @param description persisted task description.
     * @return reconstructed event task.
     * @throws PenguinException if the record, date/time, or ordering is invalid.
     */
    private static Task parseEvent(String[] parts, String description)
            throws PenguinException {
        if (parts.length != 5 || parts[3].isBlank() || parts[4].isBlank()) {
            throw new PenguinException("Invalid event data.");
        }
        LocalDateTime from = DateTimeUtil.parseDateTime(parts[3]);
        LocalDateTime to = DateTimeUtil.parseDateTime(parts[4]);
        if (!to.isAfter(from)) {
            throw new PenguinException("The start time must be before the end time.");
        }
        return new Event(description, from, to);
    }
}
