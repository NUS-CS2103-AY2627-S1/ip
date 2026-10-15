package gunna.task;

/**
 * Represents a task with a description and completion status.
 * This is the base class for specific task types like Todo, Deadline, and Event.
 */
public class Task {
    // Storage format constants
    public static final String FIELD_SEPARATOR = " | ";
    public static final String STATUS_DONE = "1";
    public static final String STATUS_NOT_DONE = "0";

    protected String description;
    protected boolean isDone;

    /**
     * Creates a new Task with the given description.
     * The task is initially marked as not done.
     *
     * @param description The description of the task.
     */
    public Task(String description) {
        assert description != null : "Task description cannot be null";
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the status icon for the task.
     *
     * @return "X" if the task is done, " " (space) if not done.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /**
     * Marks the task as done.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks the task as not done.
     */
    public void markAsNotDone() {
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
     * Returns whether the task is marked as done.
     *
     * @return true if the task is done, false otherwise.
     */
    public boolean isDone() {
        return this.isDone;
    }

    /**
     * Converts the task to a file-safe format for storage.
     * Default implementation for base Task class (not typically used directly).
     *
     * @return A pipe-separated string representation of the task.
     */
    public String toFileFormat() {
        return "T" + FIELD_SEPARATOR + (isDone ? STATUS_DONE : STATUS_NOT_DONE)
                + FIELD_SEPARATOR + description;
    }

    /**
     * Returns a string representation of the task for display.
     * Format: [status icon] description
     *
     * @return The string representation of the task.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
