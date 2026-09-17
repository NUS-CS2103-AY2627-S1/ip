package nori.task;

/**
 * Represents a task and whether it has been completed.
 */
public abstract class Task {
    /** Description shown to the user. */
    protected final String description;
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description Description of the task.
     */
    protected Task(String description) {
        this.description = description;
    }

    /**
     * Marks this task as complete.
     */
    public void mark() {
        isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void unmark() {
        isDone = false;
    }

    /**
     * Reports whether this task is complete.
     *
     * @return True if the task is complete.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns this task's description.
     *
     * @return Task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Converts this task to the text representation used by storage.
     *
     * @return Storage representation of this task.
     */
    public abstract String toStorageString();

    private String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the single-character icon for this task type.
     *
     * @return Task type icon.
     */
    protected abstract String getTypeIcon();

    /**
     * Returns the task type, completion status, and description for display.
     *
     * @return Display representation of this task.
     */
    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description;
    }
}
