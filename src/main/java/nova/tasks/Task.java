package nova.tasks;

/**
 * Represents a generic task with a description and completion status.
 */
public class Task {

    protected String description;
    protected boolean isDone;

    /**
     * Creates a new task with the given description.
     * The task is initially marked as not done.
     *
     * @param description description of the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the status icon of the task.
     *
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Marks the task as completed.
     */
    public void markDone() {
        isDone = true;
    }

    /**
     * Marks the task as not completed.
     */
    public void unmarkUndone() {
        isDone = false;
    }

    /**
     * Converts the task into a format suitable for saving to a file.
     *
     * @return file representation of the task
     */
    public String toFileString() {
        return "";
    }

    /**
     * Returns a user-friendly representation of the task.
     *
     * @return formatted task description and status
     */

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
