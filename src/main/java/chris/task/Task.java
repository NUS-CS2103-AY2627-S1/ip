package chris.task;

/**
 * Represents a task with a description and completion status.
 */
public abstract class Task {
    /** Description of this task. */
    protected String description;

    /** Whether this task has been completed. */
    protected boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns the symbol used to display this task's completion status.
     *
     * @return {@code X} if completed, or a space otherwise.
     */
    public String getStatusIcon() {
        return this.isDone ? "X" : " ";
    }

    /**
     * Returns whether this task's description contains the specified keyword.
     *
     * @param keyword Keyword to search for.
     * @return {@code true} if the description contains the keyword.
     */
    public boolean containsKeyword(String keyword) {
        return this.description.contains(keyword);
    }

    /**
     * Returns whether another task has the same type and description.
     *
     * <p>Task descriptions are compared without considering letter case so that
     * minor capitalization differences do not create duplicate tasks.
     *
     * @param otherTask Task to compare with this task.
     * @return {@code true} if both tasks have the same basic details.
     */
    boolean hasSameDetails(Task otherTask) {
        return otherTask != null
                && this.getClass() == otherTask.getClass()
                && this.description.equalsIgnoreCase(otherTask.description);
    }

    /**
     * Returns the numeric completion status used in the data file.
     *
     * @return {@code 1} if completed, or {@code 0} otherwise.
     */
    protected String getStatusNumber() {
        return this.isDone ? "1" : "0";
    }

    /**
     * Returns this task in the format used for persistent storage.
     *
     * @return Serialized representation of this task.
     */
    public abstract String toDataString();

    /**
     * Returns this task in its display format.
     *
     * @return The status icon followed by the task description.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + this.description;
    }
}
