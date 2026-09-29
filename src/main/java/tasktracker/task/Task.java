package tasktracker.task;

/**
 * Represents a generic task in the application.
 * <p>
 * Serves as the base class for specific task types like {@code ToDo}, {@code Deadline}, and {@code Event}.
 * <p>
 * Credits: Skeleton class template provided from CS2103T website.
 */
public abstract class Task {
    private final String description;
    private boolean isDone;

    /**
     * Constructs a {@code Task} instance with specified description and sets completion status to {@code false}.
     *
     * @param description Text describing the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Gets the completion status icon representing whether the task is done.
     *
     * @return String {@code "X"} if done, or a single space if undone.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /**
     * Formats the task into a plain-text string for saving to a file.
     *
     * @return A pipe-delimited string representing the task for file storage.
     */
    public abstract String toFileFormat();

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as uncompleted.
     */
    public void markAsUndone() {
        this.isDone = false;
    }

    /**
     * Gets the description of the task.
     *
     * @return The task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the common prefix for disk serialization.
     *
     * @return A pipe-delimited string containing the completion flag and task description.
     */
    protected String toFileFormatPrefix() {
        return (this.isDone ? "1" : "0") + " | " + this.description;
    }

    /**
     * Returns the string representation of the task including its completion status.
     *
     * @return Formatted string representation of the task.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + this.description;
    }
}
