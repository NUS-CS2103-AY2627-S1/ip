package alice;

/**
 * Represents an abstract base class for all tasks in the system.
 * Contains common fields such as description and completion status,
 * and provides abstract methods for subclasses to define their specific type.
 */
public abstract class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Constructs a Task with the given description.
     * The task is initially marked as not done.
     *
     * @param description The textual description of the task.
     */
    public Task(String description) {
        assert description != null : "Task description cannot be null";
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the description of the task.
     *
     * @return The task description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns the status icon representing whether the task is done.
     *
     * @return "X" if the task is done, otherwise a space character " ".
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /**
     * Checks if the task is marked as done.
     *
     * @return true if the task is done, false otherwise.
     */
    public boolean isDone() {
        return this.isDone;
    }

    /**
     * Toggles the done status of the task.
     * If it was done, it becomes not done, and vice versa.
     */

    public void toggleStatus() {
        this.isDone = !this.isDone;
    }

    /**
     * Returns the type icon for the task (e.g., "T", "D", "E").
     * Must be implemented by concrete subclasses.
     *
     * @return The task type icon string.
     */
    public abstract String getTaskTypeIcon();

    /**
     * Creates an independent copy of this task, including its completion status.
     *
     * @return A copy of this task.
     */
    public abstract Task copy();

    /**
     * Returns the string representation of the task.
     * The format is: "[statusIcon] description" (e.g., "[X] read book").
     *
     * @return The formatted task string.
     */
    @Override
    public String toString() {
        return String.format("[%s] %s", this.getStatusIcon(), this.description);
    }
}
