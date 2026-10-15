package benbot;

/** A task stored by BenBot. */
public class Task {
    /** The text that describes the task. */
    private final String description;

    /** The category that determines the task's list symbol. */
    private final TaskType type;

    /** Whether the task has been completed. */
    private TaskStatus status;

    /**
     * Creates a basic to-do task.
     *
     * @param description the text that describes the task.
     */
    public Task(String description) {
        this(description, TaskType.TODO);
    }

    /**
     * Creates a task with the specified type.
     *
     * @param description the text that describes the task.
     * @param type the task's category.
     */
    protected Task(String description, TaskType type) {
        assert description != null && !description.isBlank()
                : "A task must have a non-blank description";
        assert type != null : "A task must have a type";
        this.description = description;
        this.type = type;
        this.status = TaskStatus.NOT_DONE;
    }

    /**
     * Returns the icon for the current completion status.
     *
     * @return the icon that represents this task's completion status.
     */
    protected String getStatusIcon() {
        return status.getIcon();
    }

    /** Marks this task as completed. */
    public void markDone() {
        status = TaskStatus.DONE;
    }

    /** Marks this task as not completed. */
    public void markUndone() {
        status = TaskStatus.NOT_DONE;
    }

    /**
     * Returns whether this task has been marked as completed.
     *
     * @return {@code true} when the task has been marked as complete.
     */
    public boolean isDone() {
        return status == TaskStatus.DONE;
    }

    /**
     * Returns this task as the command used to recreate it from stored data.
     *
     * @return the storage command for this task.
     */
    public String toStorageString() {
        return "todo " + description;
    }

    /**
     * Returns this task's description for subclasses that build storage commands.
     *
     * @return the description of this task.
     */
    protected String getDescription() {
        return description;
    }

    /** Returns the task in the format shown by BenBot. */
    @Override
    public String toString() {
        return type.getSymbol() + getStatusIcon() + " " + description;
    }
}
