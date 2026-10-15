package alex.task;

/**
 * Represents a task with a description and completion status.
 */
public class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates an incomplete task with the specified description.
     *
     * @param description description of the task.
     */
    public Task(String description) {
        assert description != null : "Task description cannot be null";
        assert !description.isBlank() : "Task description cannot be blank";

        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns an icon representing the task's completion status.
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
    public void markAsUndone() {
        isDone = false;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return {@code true} if this task is complete.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns this task in the format used by the data file.
     *
     * @return the serialized task.
     */
    public String toDataString() {
        return "T | " + getDoneFlag() + " | " + description;
    }

    /**
     * Returns the numeric completion flag used in the data file.
     *
     * @return {@code 1} if complete, or {@code 0} otherwise.
     */
    protected int getDoneFlag() {
        return isDone ? 1 : 0;
    }

    /**
     * Returns this task's description.
     *
     * @return the task description.
     */
    protected String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
