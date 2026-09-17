package nubish.tasks;

/**
 * Represents a task with a description and completion status.
 */
public class Task {
    private String description;
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description description of the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Creates a task with the given completion status and description.
     *
     * @param isDone whether the task has been completed
     * @param description description of the task
     */
    public Task(boolean isDone, String description) {
        this.description = description;
        this.isDone = isDone;

        assert this.description != null : "task description was null";
    }

    /**
     * Returns the task description.
     *
     * @return description of the task
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns the display icon for the task's completion status.
     *
     * @return {@code X} if the task is done, or a blank space otherwise
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as not completed.
     */
    public void unmarkAsDone() {
        this.isDone = false;
    }

    /**
     * Returns the storage representation of this task.
     *
     * @return formatted task string with its completion status and description
     */
    public String saveString() {
        return String.format("%d | %s", isDone ? 1 : 0, this.description);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", this.getStatusIcon(), this.description);
    }
}
