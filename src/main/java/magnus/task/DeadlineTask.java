package magnus.task;

import java.time.LocalDateTime;
import java.util.Objects;

import magnus.parser.DateTimeFormats;
import magnus.parser.DateTimeParser;

/**
 * Represents a task that must be completed by a specified deadline.
 */
public class DeadlineTask extends Task {
    private static final DateTimeParser DEADLINE_PARSER = new DateTimeParser();

    private final LocalDateTime deadline;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description The description of the task.
     * @param deadline The deadline by which the task should be completed.
     */
    public DeadlineTask(String description, String deadline) {
        this(description, DEADLINE_PARSER.parseDateTime(deadline, "deadline"));
    }

    /**
     * Creates an incomplete deadline task with an already-parsed deadline.
     *
     * @param description The description of the task.
     * @param deadline The deadline by which the task should be completed.
     */
    public DeadlineTask(String description, LocalDateTime deadline) {
        super(description);
        this.deadline = Objects.requireNonNull(deadline);
    }

    /**
     * Returns the deadline as a date-time value.
     *
     * @return The task's deadline.
     */
    public LocalDateTime getDeadline() {
        return this.deadline;
    }

    /**
     * Formats the deadline in the same canonical form accepted from user input and storage.
     *
     * @return The formatted deadline.
     */
    private String formatDeadlineForStorage() {
        return this.deadline.format(DateTimeFormats.FORMATTER_DATE_TIME);
    }

    /**
     * Formats the deadline for display with an English abbreviated month.
     *
     * @return The display-formatted deadline.
     */
    private String formatDeadlineForDisplay() {
        return this.deadline.format(DateTimeFormats.FORMATTER_DISPLAY_DATE_TIME);
    }

    @Override
    protected TaskType getTaskType() {
        return TaskType.DEADLINE;
    }

    @Override
    protected boolean hasSameSchedule(Task other) {
        DeadlineTask otherDeadline = (DeadlineTask) other;
        return this.deadline.equals(otherDeadline.deadline);
    }

    /**
     * Serializes this deadline task into the format used by persistent storage.
     *
     * @return The serialized deadline-task data.
     */
    @Override
    public String serialize() {
        return String.format("%s,%d,%s,%s",
                getTaskType().getStorageCode(),
                getCompletionStatusCode(), encodeDataField(getDescription()),
                encodeDataField(formatDeadlineForStorage()));
    }

    /**
     * Returns a string representation containing the deadline-task marker, completion status,
     * description, and deadline.
     *
     * @return The string representation of this deadline task.
     */
    @Override
    public String toString() {
        return String.format("[D]%s (by: %s)", super.toString(), formatDeadlineForDisplay());
    }
}
