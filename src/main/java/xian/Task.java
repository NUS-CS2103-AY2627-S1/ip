package xian;

/**
 * Represents a generic task with a description and a completion status.
 * This is the base class for all specific task types such as
 * {@link Todo}, {@link Deadline}, and {@link Event}.
 */
public class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Creates a new Task with the given description.
     * The task is initially marked as not done.
     *
     * @param description the description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the description of this task.
     *
     * @return the task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Updates this task's description.
     *
     * @param description the new task description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the status icon representing whether this task is done.
     *
     * @return {@code "X"} if the task is done, or a blank space {@code " "} otherwise.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /**
     * Returns the status code used when saving this task to storage.
     *
     * @return {@code "1"} if the task is done, or {@code "0"} otherwise.
     */
    public String getStatusCode() {
        return (isDone ? "1" : "0");
    }

    /**
     * Marks this task as done.
     */
    public void mark() {
        this.isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void unmark() {
        this.isDone = false;
    }

    /**
     * Returns the string representation of this task,
     * consisting of its status icon and description.
     *
     * @return the formatted string representation of this task.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
