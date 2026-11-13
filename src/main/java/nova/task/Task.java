package nova.task;

/**
 * Represents a task tracked by Nova.
 */
public abstract class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates an incomplete task with the specified description.
     *
     * @param description Description of the task.
     */
    protected Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Marks this task as completed.
     */
    public void markDone() {
        isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void unmarkDone() {
        isDone = false;
    }

    /**
     * Returns whether this task is completed.
     *
     * @return True if the task is completed.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the task description.
     *
     * @return Task description.
     */
    protected String getDescription() {
        return description;
    }

    /**
     * Returns a storage representation of this task.
     *
     * @return Serialized task data.
     */
    public abstract String toDataString();

    /**
     * Returns the icon representing this task's type.
     *
     * @return Task-type icon.
     */
    protected abstract String getTypeIcon();

    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + (isDone ? "X" : " ") + "] " + description;
    }
}
