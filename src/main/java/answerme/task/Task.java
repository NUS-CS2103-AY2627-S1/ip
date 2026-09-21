package answerme.task;

/**
 * Represents a task that can be marked as complete or incomplete.
 */
public abstract class Task {
    protected String description;
    protected boolean isComplete;

    /**
     * Constructs a new task with a description specified. Its default status
     * is incomplete (i.e. {@code false}).
     *
     * @param description The task description.
     */
    public Task(String description) {
        assert description != null;
        this.description = description;
        this.isComplete = false;
    }

    /**
     * Creates a task with the specified description and completion status.
     *
     * @param description The task description.
     * @param isComplete The completion status of the task.
     */
    public Task(String description, boolean isComplete) {
        assert description != null;
        this.description = description;
        this.isComplete = isComplete;
    }

    /**
     * Returns the icon representing the current task's completion status.
     *
     * @return {@code "X"} if the task is complete, otherwise a
     *                     {@code " "} character.
     */
    public String getStatusIcon() {
        return isComplete() ? "X" : " ";
    }

    // accessors
    public String getDescription() {
        return this.description;
    }

    public void setCompletionStatus(boolean isComplete) {
        this.isComplete = isComplete;
    }

    public boolean isComplete() {
        return this.isComplete;
    }

    /**
     * Returns this task's status icon followed by its description.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }

    /**
     * Formats the (sub)task into its storage format.
     *
     * @param type The sub-task type.
     * @return The formatted task.
     */
    protected String getLogFormat(String type) {
        String status = isComplete ? "Complete" : "Incomplete";

        assert type.equals("Todo")
                || type.equals("Deadline")
                || type.equals("Event")
                : "Unexpected task type: " + type;

        return type + " | " + status + " | " + description;
    }

    /**
     * Returns this task's data in the format used for storage.
     *
     * @return The storage representation of this task.
     */
    public abstract String toStorageFormat();

    /**
     * Returns whether the current task is considered to be a duplicate
     * of the other specified task (i.e. same type and description)
     *
     * @param other The task to compare against.
     * @return {@code true} if both tasks have the same type and description,
     *         while ignoring case.
     */
    public boolean isDuplicateOf(Task other) {
        return getClass() == other.getClass()
                && description.equalsIgnoreCase(other.description);
    }
}
