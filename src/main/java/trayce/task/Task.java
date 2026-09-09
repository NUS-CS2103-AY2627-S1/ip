package trayce.task;

/**
 * Represents a task entered by the user.
 */
public class Task {
    protected String description;
    protected boolean isDone;
    protected TaskType type;

    /**
     * Creates a new incomplete task.
     *
     * @param description the text describing the task
     */
    public Task(String description) {
        this(description, TaskType.TODO);
    }

    /**
     * Creates a new incomplete task of the specified type.
     *
     * @param description the text describing the task
     * @param type the kind of task
     */
    protected Task(String description, TaskType type) {
        this.description = description;
        this.isDone = false;
        this.type = type;
    }

    /** Marks this task as done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the symbol used to display this task's status.
     *
     * @return {@code X} when done, otherwise a blank space
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
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
     * Returns whether this task has been completed.
     *
     * @return {@code true} if the task is marked done
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns this task's category for saving it to disk.
     *
     * @return the task type
     */
    public TaskType getType() {
        return type;
    }

    /**
     * Returns the type icon shown before the task status.
     *
     * @return {@code T} for a basic todo task
     */
    public String getTypeIcon() {
        return type.getIcon();
    }

    /**
     * Returns any date or time details for display.
     *
     * @return an empty string for a todo task
     */
    public String getDateTimeDetails() {
        return "";
    }
}
