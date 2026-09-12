package alfred.task;

/**
 * Represents one task in Alfred's task list.
 */
public class Task {
    protected final String description;
    protected boolean isDone;
    private final TaskType type;

    /**
     * Creates an incomplete task with the given type and description.
     *
     * @param type kind of task being created
     * @param description text describing the task
     */
    protected Task(TaskType type, String description) {
        this.type = type;
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the status symbol displayed beside the task.
     *
     * @return {@code "X"} when the task is complete, otherwise a space
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Marks this task as complete. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void markAsNotDone() {
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
     * Returns the type of this task for storage purposes.
     *
     * @return the kind of this task
     */
    public TaskType getType() {
        return type;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return {@code true} when the task is complete
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns whether another task has the same identity for duplicate detection.
     *
     * @param other task to compare with this task
     * @return whether both tasks have the same type and description
     */
    public boolean hasSameIdentityAs(Task other) {
        return other != null && type == other.type && description.equals(other.description);
    }

    /**
     * Returns this task in the format used in Alfred's task list.
     *
     * @return the task type, status icon, and description
     */
    @Override
    public String toString() {
        return "[" + type.getDisplayCode() + "][" + getStatusIcon() + "] " + description;
    }
}
