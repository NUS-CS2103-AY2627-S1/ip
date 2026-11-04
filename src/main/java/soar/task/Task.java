package soar.task;

/**
 * Represents the shared state and behavior of a task.
 */
public abstract class Task {
    /** Description of what needs to be done. */
    private final String description;

    /** Whether this task has been completed. */
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description Description of what needs to be done.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the label that identifies the concrete task type.
     *
     * @return the task type label, such as {@code [T]}
     */
    public abstract String getTypeIcon();

    /**
     * Returns this task in the compact format used for persistent storage.
     *
     * @return one line that can be written to the task data file
     */
    public abstract String toDataString();

    /**
     * Returns {@code 1} for a completed task and {@code 0} otherwise.
     *
     * @return completion state in the storage format
     */
    protected String getDataStatus() {
        return isDone ? "1" : "0";
    }

    /**
     * Returns the task description for use by concrete task types.
     *
     * @return task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return {@code true} if the task is done
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Checks whether this task's description contains the supplied search text.
     *
     * @param keyword keyword or phrase to find
     * @return {@code true} when the description contains the search text
     */
    public boolean descriptionContains(String keyword) {
        return description.contains(keyword);
    }

    /**
     * Returns the icon used to show this task's completion status.
     *
     * @return {@code X} if the task is done, or a space otherwise
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not completed.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the task in the format used by the chatbot.
     *
     * @return the task type, status icon, and description
     */
    @Override
    public String toString() {
        return getTypeIcon() + "[" + getStatusIcon() + "] " + description;
    }
}
