package ruby.task;

/**
 * Represents a task and whether the user has completed it.
 */
public class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates an incomplete task with the specified description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        assert description != null : "Task description cannot be null";
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the icon representing whether this task is complete.
     *
     * @return {@code X} if complete, or a space otherwise.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Marks this task as complete.
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

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }

    /**
     * Returns this task as one line of the saved data file.
     *
     * @return The done flag and description, separated by {@code " | "}.
     */
    public String toDataString() {
        return (isDone ? TaskDataFormat.DONE_MARKER : TaskDataFormat.NOT_DONE_MARKER)
                + TaskDataFormat.PART_SEPARATOR
                + description;
    }

    /**
     * Returns whether this task has the same user-visible details as another task.
     * Completion status is excluded so it cannot create a duplicate.
     *
     * @param other Task to compare with.
     * @return Whether both tasks have the same type and description.
     */
    public boolean hasSameDetails(Task other) {
        return other != null && getClass().equals(other.getClass()) && description.equals(other.description);
    }
}
