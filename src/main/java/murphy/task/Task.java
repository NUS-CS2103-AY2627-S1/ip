package murphy.task;

import java.util.Locale;

/**
 * Represents one task in Murphy's task list.
 */
public class Task {
    /** The text describing the task. */
    protected final String description;

    /** Whether the task has been completed. */
    private boolean isDone;

    /**
     * Creates an incomplete task.
     *
     * @param description the text describing the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the icon used when displaying this task.
     *
     * @return {@code X} for a completed task, or a space otherwise
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the task description.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return {@code true} if this task is completed
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns whether this task has the same user-provided details as another task.
     * Completion status is intentionally not part of task identity.
     *
     * @param other the task to compare with
     * @return {@code true} when both tasks are todos with equivalent descriptions
     */
    public boolean hasSameDetailsAs(Task other) {
        return other != null && getClass() == other.getClass()
                && normalize(description).equals(normalize(other.description));
    }

    /**
     * Returns this task in Murphy's save-file format.
     *
     * @return a pipe-separated representation of this task
     */
    public String toDataString() {
        return "T | " + (isDone() ? "1" : "0") + " | " + escapeDataField(description);
    }

    /** Escapes characters that have a special meaning in Murphy's save-file format. */
    protected static String escapeDataField(String value) {
        return value.replace("\\", "\\\\").replace("|", "\\|");
    }

    /** Normalizes user-entered text for case-insensitive duplicate detection. */
    protected static String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }

    /** Returns the common task text, including its type and completion state. */
    @Override
    public String toString() {
        return "[T][" + getStatusIcon() + "] " + description;
    }
}
