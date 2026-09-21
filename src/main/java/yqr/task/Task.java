package yqr.task;

import java.util.Objects;

/**
 * Represents a task and whether it has been completed.
 */
public class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description description of the task.
     */
    public Task(String description) {
        this.description = Objects.requireNonNull(description, "Task description cannot be null");
        this.isDone = false;
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not completed.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the task description.
     *
     * @return task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether the task has been completed.
     *
     * @return {@code true} if the task is completed.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns whether another task has the same type and user-provided details.
     * Completion status is deliberately ignored when detecting duplicates.
     *
     * @param other task to compare.
     * @return {@code true} if both tasks have the same identifying details.
     */
    public boolean hasSameDetails(Task other) {
        return other != null
                && getClass().equals(other.getClass())
                && description.equals(other.description);
    }

    /**
     * Returns the character used to display this task's completion status.
     *
     * @return {@code X} when completed, or a space otherwise.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns a displayable version of this task.
     *
     * @return status icon followed by the task description.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
