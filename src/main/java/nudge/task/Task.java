package nudge.task;

/**
 * Represents a task and whether it has been completed.
 */
public class Task {
    /** Description of the task. */
    private final String description;

    /** Whether the task has been completed. */
    private boolean isDone;

    /**
     * Creates an incomplete task with the specified description.
     *
     * @param description description of the task.
     */
    public Task(String description) {
        assert description != null : "Task description should not be null";
        assert !description.isBlank() : "Task description should not be blank";
        this.description = description;
        this.isDone = false;
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return true if this task is done, or false otherwise.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns this task's description.
     *
     * @return task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the icon representing this task's completion state.
     *
     * @return {@code X} if the task is done, or a space otherwise.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the task formatted with its completion status.
     *
     * @return formatted task.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
