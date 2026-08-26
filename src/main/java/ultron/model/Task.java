package ultron.model;

/**
 * Represents one task and whether it has been completed.
 */
public class Task {
    protected String description;
    protected boolean isDone;
    protected TaskType type;


    /**
     * Creates an incomplete task with the given description.
     *
     * @param description the task description
     * @param type the category of task
     */
    public Task(String description, TaskType type) {
        this.description = description;
        this.isDone = false;
        this.type = type;
    }

    /**
     * Returns the status symbol used when displaying this task.
     *
     * @return {@code X} for a completed task, otherwise a space
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns whether this task has been marked as completed.
     *
     * @return {@code true} if this task is completed
     */
    public boolean isDone() {
        return isDone;
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
    public void markAsUndone() {
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
     * Returns this task's category.
     *
     * @return this task's type
     */
    public TaskType getType() {
        return type;
    }

}
