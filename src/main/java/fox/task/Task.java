package fox.task;

import java.util.Objects;

/**
 * Represents a task in Fox's task list.
 */
public class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates a new unfinished task.
     *
     * @param description the task description
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the status icon used when displaying this task.
     *
     * @return {@code X} for a completed task, or a blank space otherwise
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Marks this task as done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void markAsNotDone() {
        isDone = false;
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
     * Returns whether this task has been marked as done.
     *
     * @return {@code true} when this task is complete; otherwise {@code false}
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns whether another task has the same type and intrinsic details.
     * Completion status is excluded because it can change during a task's lifetime.
     *
     * @param other the task to compare with
     * @return {@code true} if both tasks have the same type and description
     */
    public boolean hasSameDetails(Task other) {
        return other != null
                && getClass() == other.getClass()
                && Objects.equals(description, other.description);
    }

    /**
     * Returns this task's status and description for display.
     *
     * @return the formatted task details
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
