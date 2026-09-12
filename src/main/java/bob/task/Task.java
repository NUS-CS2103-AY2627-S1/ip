package bob.task;

/**
 * Represents a task tracked by the chatbot.
 */
public abstract class Task {
    /** Description of the work represented by this task. */
    private final String description;
    private TaskStatus status;
    private final TaskType type;

    /**
     * Creates an incomplete task of the specified type.
     *
     * @param description description of the work to complete.
     * @param type kind of task.
     */
    public Task(String description, TaskType type) {
        assert description != null : "A task must have a description";
        assert type != null : "A task must have a type";
        this.description = description;
        this.status = TaskStatus.NOT_DONE;
        this.type = type;
    }

    /**
     * Returns the kind of this task.
     *
     * @return task type.
     */
    public TaskType getType() {
        return type;
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
     * Returns the current completion state of this task.
     *
     * @return task status.
     */
    public TaskStatus getStatus() {
        return status;
    }

    /**
     * Returns the icon representing the current completion state.
     *
     * @return status icon.
     */
    public String getStatusIcon() {
        return status.getIcon();
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        status = TaskStatus.DONE;
    }

    /**
     * Marks this task as not completed.
     */
    public void markAsNotDone() {
        status = TaskStatus.NOT_DONE;
    }

    /**
     * Returns a display representation containing the type, status, and description.
     *
     * @return formatted task.
     */
    @Override
    public String toString() {
        return "[" + type.getSymbol() + "][" + this.getStatusIcon() + "] " + this.getDescription();
    }
}
