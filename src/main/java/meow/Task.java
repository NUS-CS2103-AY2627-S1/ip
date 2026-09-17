package meow;

/**
 * Represents a task with a description and a completion status.
 */
public class Task {
    private final String description;
    private boolean isDone = false;

    /**
     * Creates a task with the specified description.
     *
     * @param description the description of the task
     */
    public Task(String description) {
        assert description != null : "Task description should not be null";
        this.description = description;
    }

    /**
     * Returns the task status as a value suitable for saving to a file.
     *
     * @return 1 if the task is completed, or 0 otherwise
     */
    public int getStatusForFile() {
        return isDone ? 1 : 0;
    }

    /**
     * Returns the symbol representing the completion status of the task.
     *
     * @return "X" if the task is completed, or a blank space otherwise
     */
    public String getStatusForDisplay() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the description of the task.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Marks the task as completed.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks the task as not completed.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns a string representation of the task suitable for saving to a file.
     *
     * @return the task represented in the storage format
     */
    public String toFileString() {
        return "";
    }

    /**
     * Returns whether the task has been completed.
     *
     * @return true if the task is completed, false otherwise
     */
    public boolean isDone() {
        return isDone;
    }
}
