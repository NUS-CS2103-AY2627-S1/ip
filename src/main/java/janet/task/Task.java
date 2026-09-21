package janet.task;

/**
 * Represents a task that can be stored by Janet.
 */
public abstract class Task {
    /** The text describing this task. */
    protected String description;

    /** Whether this task has been marked as done. */
    protected boolean isDone;

    /**
     * Creates a new incomplete task.
     *
     * @param description the text describing the task.
     */
    protected Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the icon that will represent this task's completion status.
     *
     * @return {@code X} for a completed task, or a blank space otherwise
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /**
     * Returns the icon identifying this task type.
     *
     * @return the icon identifying the concrete task type
     */
    public abstract String getTypeIcon();

    /**
     * Returns the task description.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the unformatted description used for storage.
     *
     * @return the task description without type-specific details
     */
    public String getRawDescription() {
        return description;
    }

    /**
     * Returns whether this task is complete.
     *
     * @return whether this task has been marked as done
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsUndone() {
        isDone = false;
    }
}
