package unicorn.task;

import java.util.Locale;

/**
 * Represents a task with a description and completion status.
 */
public class Task {
    /** Description shown to the user for this task. */
    protected String description;
    /** Whether this task has been completed. */
    protected boolean isDone;

    /**
     * Creates an incomplete task with a description.
     *
     * @param description task description
     */
    public Task(String description) {
        assert description != null : "Task description must not be null";
        assert !description.isBlank() : "Task description must not be blank";

        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the icon representing this task's completion status.
     *
     * @return {@code X} when complete, or a space otherwise
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Reports whether this task is complete.
     *
     * @return {@code true} when the task is marked as done
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the task description.
     *
     * @return task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Reports whether another task has the same type and user-entered details.
     * Completion status is intentionally ignored because it does not make a
     * task a different quest.
     *
     * @param other task to compare
     * @return {@code true} when both tasks represent the same quest
     */
    public boolean hasSameDetailsAs(Task other) {
        return other != null
                && getClass().equals(other.getClass())
                && normalizeDetail(description).equals(normalizeDetail(other.description));
    }

    /**
     * Marks this task as complete.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void markAsUndone() {
        isDone = false;
    }

    /**
     * Returns a display representation of this task.
     *
     * @return task status and description
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }

    /**
     * Normalizes user-entered details for duplicate detection.
     *
     * @param detail detail to normalize
     * @return lowercase detail with surrounding and repeated whitespace removed
     */
    protected static String normalizeDetail(String detail) {
        return detail.strip().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }
}
