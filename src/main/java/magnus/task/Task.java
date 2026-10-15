package magnus.task;

import java.util.Objects;

/**
 * Represents a task in Magnus's task list.
 * A newly created task is incomplete by default.
 */
public class Task {
    private static final String FIELD_DELIMITER = ",";
    private static final String QUOTATION_MARK = "\"";
    private static final String QUOTATION_MARK_ESCAPED = "\"\"";
    private static final int STATUS_INCOMPLETE = 0;
    private static final int STATUS_COMPLETE = 1;

    private final String description;
    private boolean isDone;

    /**
     * Creates an incomplete task with the specified description.
     *
     * @param description The description of the task.
     */
    public Task(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("A task must have a non-blank description");
        }
        if (description.contains("\n") || description.contains("\r")) {
            throw new IllegalArgumentException("A task description cannot contain line breaks");
        }
        this.description = description.strip();
        this.isDone = false;
    }

    /**
     * Returns the description of this task.
     *
     * @return The task's description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns whether this task is completed.
     *
     * @return {@code true} if this task is completed; otherwise {@code false}.
     */
    public boolean isDone() {
        return this.isDone;
    }

    /**
     * Returns whether another task has the same type, description, and scheduling details.
     * Completion status is deliberately excluded because it is mutable state, not task identity.
     *
     * @param other The task to compare with this task.
     * @return {@code true} if both tasks describe the same work.
     */
    public boolean hasSameDetails(Task other) {
        return other != null
                && getTaskType() == other.getTaskType()
                && this.description.equals(other.description)
                && hasSameSchedule(other);
    }

    /**
     * Compares scheduling details supplied by a concrete task type.
     *
     * @param other The task already known to have the same task type.
     * @return {@code true} when both tasks have the same scheduling details.
     */
    protected boolean hasSameSchedule(Task other) {
        return true;
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void markAsUndone() {
        this.isDone = false;
    }

    /**
     * Serializes this task into the format used by persistent storage.
     *
     * @return The serialized task data.
     */
    public String serialize() {
        return String.format("%s,%d,%s",
                getTaskType().getStorageCode(),
                getCompletionStatusCode(), encodeDataField(this.description));
    }

    /**
     * Returns the specific type of this task.
     *
     * @return This task's type.
     */
    protected TaskType getTaskType() {
        return TaskType.TODO;
    }

    /**
     * Returns the numeric completion status used by persistent storage.
     *
     * @return {@code 1} if the task is complete; {@code 0} otherwise.
     */
    protected int getCompletionStatusCode() {
        return this.isDone ? STATUS_COMPLETE : STATUS_INCOMPLETE;
    }

    /**
     * Escapes a task field for safe storage in the comma-separated data format.
     *
     * @param field The field to encode.
     * @return The encoded field, quoted when it contains commas or quotation marks.
     * @throws IllegalArgumentException If the field contains a line break.
     */
    protected String encodeDataField(String field) {
        Objects.requireNonNull(field);
        if (field.contains("\n") || field.contains("\r")) {
            throw new IllegalArgumentException("Task fields cannot contain line breaks");
        }
        boolean requiresQuotationMarks = field.contains(FIELD_DELIMITER)
                || field.contains(QUOTATION_MARK);
        if (!requiresQuotationMarks) {
            return field;
        }

        String escapedField = field.replace(QUOTATION_MARK, QUOTATION_MARK_ESCAPED);
        return QUOTATION_MARK + escapedField + QUOTATION_MARK;
    }

    /**
     * Returns the icon representing this task's completion status.
     *
     * @return {@code "X"} if the task is complete; a space otherwise.
     */
    private String getStatusIcon() {
        return this.isDone ? "X" : " ";
    }

    /**
     * Returns a string representation containing the task's completion
     * indicator and description.
     *
     * @return The string representation of this task.
     */
    @Override
    public String toString() {
        return String.format("[%s] %s", getStatusIcon(), this.description);
    }
}
