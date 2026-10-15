package zabud.tasks;

/**
 * Represents a task and whether it has been completed.
 */
public abstract class Task {
    /**
     * The text describing this task.
     */
    private final String description;

    /**
     * Whether this task has been completed.
     */
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description the text describing the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the task description.
     *
     * @return the task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task is completed.
     *
     * @return whether the task is done.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns task-specific values needed by the session storage layer.
     *
     * @return additional values in persistence order.
     */
    public String[] getStorageDetails() {
        return new String[0];
    }

    /**
     * Returns the task type code for persistence.
     *
     * @return the one-letter task type code.
     */
    public String getStorageType() {
        return getTaskType();
    }

    /**
     * Returns the status icon used when displaying this task.
     *
     * @return {@code "X"} for a completed task or a space otherwise.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the single-letter code that identifies this task type.
     *
     * @return the task type code.
     */
    protected abstract String getTaskType();

    /**
     * Returns the task-specific information shown after its description.
     *
     * @return the additional display details for this task.
     */
    protected String getAdditionalDetails() {
        return "";
    }

    /**
     * Returns this task in the Duke list format.
     */
    @Override
    public String toString() {
        return "[" + getTaskType() + "][" + getStatusIcon() + "] "
                + description + getAdditionalDetails();
    }
}
