package eva.task;

/**
 * Represents a task with a description and completion status.
 */
public abstract class Task {
    protected String description;
    protected boolean isDone;
    protected TaskType type;

    /**
     * Creates a task with the specified description and type.
     *
     * @param description Description of the task.
     * @param type Type of the task.
     */
    public Task(String description, TaskType type) {
        this.description = description;
        this.isDone = false;
        this.type = type;
    }

    /**
     * Returns the display symbol for the completion status.
     *
     * @return {@code X} if completed, or a blank space otherwise.
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
     * Checks whether the task description contains the specified keyword.
     *
     * @param keyword Keyword to search for.
     * @return True if the task description contains the keyword.
     */
    public boolean containsKeyword(String keyword) {
        return description.toLowerCase().contains(keyword.toLowerCase());
    }

    /**
     * Returns the status value used in the data file.
     *
     * @return {@code 1} if completed, or {@code 0} otherwise.
     */
    protected String getFileStatus() {
        return isDone ? "1" : "0";
    }

    /**
     * Returns the task representation used in the data file.
     *
     * @return Serializable representation of this task.
     */
    public abstract String toFileString();

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
