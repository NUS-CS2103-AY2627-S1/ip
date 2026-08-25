package matthew.task;

/** Represents a task with a description and completion status. */
public class Task {
    /** Text describing the task. */
    protected String description;
    /** Indicates whether the task has been completed. */
    protected boolean isDone;

    /**
     * Creates an incomplete task with the specified description.
     *
     * @param description Description of the task.
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
     * Returns whether this task has been completed.
     *
     * @return {@code true} if this task has been completed.
     */
    public boolean isDone() {
        return isDone;
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
     * Returns {@code X} for a completed task or a space otherwise.
     *
     * @return Status icon for this task.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Returns the task formatted with its completion status. */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
