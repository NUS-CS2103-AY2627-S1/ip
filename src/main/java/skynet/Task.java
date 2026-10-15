package skynet;

/**
 * Represents a task with a description and completion status.
 */
public abstract class Task {
    private final String description;
    private TaskStatus status;

    /**
     * Creates a task with a description.
     *
     * @param description the description of the task
     */
    public Task(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Task description cannot be empty.");
        }
        if (description.contains(" | ") || description.contains("\n")
                || description.contains("\r")) {
            throw new IllegalArgumentException(
                    "Task descriptions cannot contain ' | ' or line breaks.");
        }
        this.description = description;
        this.status = TaskStatus.NOT_DONE;
    }

    /**
     * Marks a task as complete.
     */
    public void markAsDone() {
        status = TaskStatus.DONE;
    }

    /**
     * Unmarks a task as incomplete.
     */
    public void markAsNotDone() {
        status = TaskStatus.NOT_DONE;
    }

    /**
     * Gets the icon of the task.
     *
     * @return icon as a string
     */
    public String getStatusIcon() {
        return status.getIcon();
    }

    /**
     * Gets the description of the task.
     *
     * @return description as a string
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether the task is completed.
     *
     * @return true if the task is completed, otherwise false
     */
    public boolean isDone() {
        return status == TaskStatus.DONE;
    }

    /**
     * Creates a deep copy of this task.
     *
     * @return a new Task instance with identical state
     */
    public abstract Task copy();

    /**
     * Returns a string representation of the task.
     *
     * @return the task represented as a string
     */
    public abstract String toString();
}
