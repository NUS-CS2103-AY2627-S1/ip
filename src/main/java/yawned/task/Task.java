package yawned.task;

/**
 * Represents a task and whether it has been completed.
 */
public class Task {
    private final String description;
    private TaskStatus status;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description Task description.
     */
    public Task(String description) {
        this.description = description;
        this.status = TaskStatus.NOT_DONE;
    }

    /** Marks this task as complete. */
    public void markAsDone() {
        this.status = TaskStatus.DONE;
    }

    /** Marks this task as incomplete. */
    public void markAsUndone() {
        this.status = TaskStatus.NOT_DONE;
    }

    /**
     * Returns this task's description for storage.
     *
     * @return Task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns this task's completion status for storage.
     *
     * @return Current completion status.
     */
    public TaskStatus getStatus() {
        return status;
    }

    /**
     * Returns the task description together with its completion status.
     *
     * @return Task status and description.
     */
    @Override
    public String toString() {
        return status.getIcon() + " " + description;
    }
}
