package xerxes.task;

import java.util.Locale;
import java.util.Objects;

/**
 * Represents a task with a description and completion status.
 * Subclasses add task-specific details such as a deadline or event duration.
 */
public class Task {
    /** Description supplied by the user for this task. */
    private final String taskName;

    /** Whether this task has been completed. */
    private boolean isCompleted;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param taskName Description of the task.
     */
    public Task(String taskName) {
        assert taskName != null : "A task must have a non-null description";
        this.taskName = taskName;
    }

    /** Marks this task as completed. */
    public void markCompleted() {
        this.isCompleted = true;
    }

    /** Marks this task as incomplete. */
    public void markUncompleted() {
        this.isCompleted = false;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return True if the task is completed, otherwise false.
     */
    public boolean isCompleted() {
        return this.isCompleted;
    }

    /**
     * Returns the task description.
     *
     * @return Description of this task.
     */
    public String getTaskName() {
        return this.taskName;
    }

    /**
     * Checks whether another task has the same type and description.
     * Completion status is intentionally excluded from task equality.
     *
     * @param other Object to compare with this task.
     * @return True if both objects represent equivalent tasks.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        Task task = (Task) other;
        return taskName.equalsIgnoreCase(task.taskName);
    }

    /**
     * Returns a hash code based on the task type and description.
     *
     * @return Hash code for this task.
     */
    @Override
    public int hashCode() {
        return Objects.hash(getClass(), taskName.toLowerCase(Locale.ROOT));
    }

    /**
     * Returns a display representation containing this task's completion status and description.
     *
     * @return Formatted task description.
     */
    @Override
    public String toString() {
        if (this.isCompleted) {
            return "[X] " + this.taskName;
        } else {
            return "[ ] " + this.taskName;
        }
    }
}
