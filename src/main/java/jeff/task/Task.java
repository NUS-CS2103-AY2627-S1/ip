package jeff.task;

/**
 * Represents a task that can be marked as completed.
 */
public class Task {
    /**
     * Description of this task.
     */
    protected String description;

    /**
     * Whether this task has been completed.
     */
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

    /**
     * Returns the task's completion-status icon.
     *
     * @return Completion-status icon.
     */
    public String getStatus() {
        return isDone ? "[X] " : "[ ] ";
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
     * Marks the task as completed.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks the task as incomplete.
     */
    public void unmarkAsDone() {
        isDone = false;
    }

    @Override
    public String toString() {
        return getStatus() + " " + description;
    }

    /**
     * Returns whether the task is completed.
     *
     * @return True if the task is completed.
     */
    public boolean isDone() {
        return isDone;
    }
}
