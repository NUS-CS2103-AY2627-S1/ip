package grower.tasks;

import java.time.LocalDateTime;

/**
 * Represents a task with a description and completion status.
 */
public abstract class Task {
    /** Description displayed to the user and saved with the task. */
    private final String description;

    /** Whether the task has been completed. */
    private boolean isCompleted;

    /**
     * Creates an incomplete task with the specified description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        this.description = description;
        isCompleted = false;
    }

    /**
     * Marks this task as not completed.
     */
    public void unmark() {
        isCompleted = false;
    }

    /**
     * Marks this task as completed.
     */
    public void mark() {
        isCompleted = true;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    /**
     * Returns the serialized representation of this task.
     *
     * @return Serialized task data.
     */
    public abstract String toFileString();

    /**
     * Returns the time used for sorting, or null for a dateless task.
     *
     * @return Sorting time, or null if the task has no date.
     */
    public LocalDateTime getSortTime() {
        return null;
    }

    /**
     * Returns a display string containing the completion status and description.
     *
     * @return Display representation of this task.
     */
    @Override
    public String toString() {
        String tick = isCompleted ? "X" : " ";

        return String.format("[%s] %s", tick, description);
    }
}
