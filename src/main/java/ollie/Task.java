package ollie;

import java.util.Locale;

/**
 * Represents a task with a description and completion status.
 */
public class Task {
    /** Description supplied by the user. */
    private final String description;
    /** Whether this task has been completed. */
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Marks this task as completed.
     */
    public void mark() {
        isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void unmark() {
        isDone = false;
    }

    /**
     * Returns the task description.
     *
     * @return Task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task is completed.
     *
     * @return True if this task is completed.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns whether another task has the same type and description.
     * Completion status is deliberately ignored when identifying duplicates.
     *
     * @param other Task to compare with this task.
     * @return True if both tasks represent the same task details.
     */
    public boolean hasSameDetails(Task other) {
        return other != null
                && getClass().equals(other.getClass())
                && normalizeDescription(description).equals(normalizeDescription(other.description));
    }

    /**
     * Returns the completion status and description of this task.
     *
     * @return Formatted task.
     */
    @Override
    public String toString() {
        return "[" + (isDone ? "X" : " ") + "] " + description;
    }

    private String normalizeDescription(String text) {
        return text.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }
}
