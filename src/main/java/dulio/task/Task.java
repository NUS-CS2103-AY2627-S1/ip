package dulio.task;

/**
 * Represents a task with a description and completion status.
 */
public class Task {
    /**
     * The task description.
     */
    protected String description;
    /**
     * Whether the task has been completed.
     */
    protected boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description The task description.
     */
    public Task(String description) {
        assert description != null : "Task description must not be null";
        assert !description.isBlank() : "Task description must not be blank";
        this.description = description;
        this.isDone = false;
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void markAsNotDone() {
        this.isDone = false;
    }

    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    public boolean isDone() {
        return isDone;
    }

    public String getDescription() {
        return description;
    }

    public String getTypeIcon() {
        return "T";
    }

    /**
     * Returns the task description for display.
     *
     * @return The task description.
     */
    @Override
    public String toString() {
        return description;
    }
}
