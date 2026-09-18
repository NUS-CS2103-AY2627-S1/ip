package phin;

/**
 * Represents a task and its completion status.
 */
public class Task {
    /**
     * Description displayed to users and written to storage.
     */
    protected String description;
    /**
     * Whether the task has been completed.
     */
    protected boolean isDone;

    /**
     * Creates a task that has not been completed yet.
     *
     * @param description description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the icon used to display the task's completion status.
     *
     * @return {@code X} when done, or a space when not done.
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
     * Formats the task for display in the task list and command responses.
     *
     * @return status icon followed by the task description.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
