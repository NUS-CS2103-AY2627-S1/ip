package dave.task;

/**
 * Represents an abstract task tracked by the chatbot.
 * Serves as a base class for specific task types such as Todo, Deadline, and Event.
 */
public abstract class Task {

    /** Text description of the task. */
    private final String description;
    /** Completion status of the task. */
    private boolean isDone;

    /**
     * Constructs a new Task with the specified description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns whether the task has been completed.
     *
     * @return True if the task is completed, false otherwise.
     */
    public boolean isDone() {
        return this.isDone;
    }

    /**
     * Updates the completion status of the task.
     *
     * @param isDone New completion status.
     */
    public void setDone(boolean isDone) {
        this.isDone = isDone;
    }

    /**
     * Returns the description of the task.
     *
     * @return Task description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns the status icon indicating whether the task is completed.
     *
     * @return "X" if completed, otherwise a single whitespace.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /**
     * Returns whether the task has time constraints and can be snoozed.
     *
     * @return True if the task can be snoozed, false otherwise.
     */
    public boolean canSnooze() {
        return false;
    }

    /**
     * Formats the task as a string suitable for persistent storage.
     *
     * @return Serialized string representing the task.
     */
    public abstract String toFileFormat();

    /**
     * Determines whether this task is logically identical to another task.
     * Subclasses define identity based on task-specific attributes.
     *
     * @param other Other task to compare against.
     * @return True if tasks are identical, false otherwise.
     */
    public abstract boolean isSameTask(Task other);

    @Override
    public String toString() {
        return String.format("[%s] %s", this.getStatusIcon(), this.description);
    }
}
