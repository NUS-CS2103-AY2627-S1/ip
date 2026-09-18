package lloyd.task;

/**
 * Represents a task and whether it has been completed.
 */
public class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /** Marks this task as completed. */
    public void mark() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void unmark() {
        isDone = false;
    }

    /**
     * Returns the task description for storage and other non-display uses.
     *
     * @return Task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Reports whether this task has been completed.
     *
     * @return {@code true} when the task is complete.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns a displayable representation of this task.
     *
     * @return Task status and description.
     */
    @Override
    public String toString() {
        String status = isDone ? "X" : " ";
        return "[" + status + "] " + description;
    }
}
