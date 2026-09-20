package loki.model;

import java.time.LocalDateTime;

import loki.parser.DateTimeParser;
import loki.parser.TaskUpdate;

/**
 * A task that must be completed before a specified time.
 */
public class Deadline extends Task {
    private final LocalDateTime due;

    /**
     * Creates a new unfinished deadline task.
     *
     * @param title the task description.
     * @param due the deadline, kept in the user's display format.
     */
    public Deadline(String title, String due) {
        this(title, DateTimeParser.parseUserInput(due));
    }

    /**
     * Creates an unfinished deadline from a date/time value.
     *
     * @param title the task description
     * @param due the deadline
     */
    public Deadline(String title, LocalDateTime due) {
        super(title);
        this.due = validateDateTime(due, "Deadline");
    }

    /**
     * Creates a deadline from user-entered date/time text and a completion status.
     *
     * @param title the task description
     * @param done whether the task has been completed
     * @param due the deadline in a supported user-entered format
     */
    public Deadline(String title, boolean done, String due) {
        this(title, done, DateTimeParser.parseUserInput(due));
    }

    /**
     * Creates a deadline from a date/time value and a completion status.
     *
     * @param title the task description
     * @param done whether the task has been completed
     * @param due the deadline
     */
    public Deadline(String title, boolean done, LocalDateTime due) {
        super(title, done);
        this.due = validateDateTime(due, "Deadline");
    }

    /**
     * Creates a deadline from stored date/time text and a numeric status.
     *
     * @param title the task description
     * @param done the completion status, which must be {@code 0} or {@code 1}
     * @param due the deadline in ISO storage format
     */
    public Deadline(String title, int done, String due) {
        this(title, done, DateTimeParser.parseStored(due));
    }

    /**
     * Creates a deadline from a date/time value and a numeric status.
     *
     * @param title the task description
     * @param done the completion status, which must be {@code 0} or {@code 1}
     * @param due the deadline
     */
    public Deadline(String title, int done, LocalDateTime due) {
        super(title, done);
        this.due = validateDateTime(due, "Deadline");
    }

    /**
     * Ensures that a deadline date/time is present.
     *
     * @param value the date/time to validate
     * @param fieldName the field name used in an error message
     * @return the validated date/time
     * @throws IllegalArgumentException if {@code value} is {@code null}
     */
    private static LocalDateTime validateDateTime(LocalDateTime value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
        return value;
    }

    /**
     * Formats this task with its deadline.
     *
     * @return the formatted deadline task.
     */
    @Override
    public String toString() {
        return String.format("[D]%s (by: %s)", super.toString(),
                DateTimeParser.formatForDisplay(due));
    }

    /**
     * Converts this deadline task into the storage format.
     *
     * @return the serialized deadline task.
     */
    @Override
    public String saveString() {
        int status = isDone() ? 1 : 0;
        return String.format("D | %d | %s | %s", status, getTitle(),
                DateTimeParser.formatForStorage(due));
    }

    /**
     * Creates an updated deadline while preserving its completion status.
     *
     * @param update the replacement fields.
     * @return the updated deadline.
     * @throws IllegalArgumentException if the update contains event fields.
     */
    @Override
    public Task updatedWith(TaskUpdate update) {
        if (update.getEventStart().isPresent() || update.getEventEnd().isPresent()) {
            throw new IllegalArgumentException("A deadline does not support event fields");
        }
        String title = update.getTitle().orElse(getTitle());
        LocalDateTime updatedDue = update.getDeadline().orElse(due);
        return new Deadline(title, isDone(), updatedDue);
    }
}
