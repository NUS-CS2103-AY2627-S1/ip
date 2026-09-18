package rene.task;

/**
 * Represents the shared description and completion status of a task.
 */
public abstract class Task {
    private final String description;
    private TaskStatus status;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description the text describing the task.
     */
    public Task(String description) {
        this.description = description;
        this.status = TaskStatus.NOT_DONE;
    }

    /**
     * Returns the symbol used to display this task's completion status.
     *
     * @return {@code X} when completed, or a space when incomplete.
     */
    public String getStatusIcon() {
        return status.getIcon();
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return {@code true} if this task is marked as done.
     */
    public boolean isDone() {
        return status == TaskStatus.DONE;
    }

    /**
     * Returns the text that describes this task.
     *
     * @return the task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        status = TaskStatus.DONE;
    }

    /**
     * Marks this task as incomplete.
     */
    public void unmarkAsDone() {
        status = TaskStatus.NOT_DONE;
    }

    /**
     * Returns the type of this task.
     *
     * @return the task type.
     */
    public abstract TaskType getTaskType();

    /**
     * Creates an independent copy of this task, including its completion status.
     *
     * @return a copy that can be changed without affecting this task.
     */
    abstract Task copy();

    /**
     * Returns this task in the chatbot's display format.
     *
     * @return the task type, completion status, and description.
     */
    @Override
    public String toString() {
        return "[" + getTaskType().getIcon() + "][" + getStatusIcon() + "] " + description;
    }
}
