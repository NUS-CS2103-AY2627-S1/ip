package orbit.task;

/**
 * Represents a task with a description and completion status.
 */
public class Task {
    /** Description shown to the user. */
    protected String description;

    /** Whether this task is completed. */
    protected boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description description shown to the user
     */
    public Task(String description) {
        this(description, false);
    }

    /**
     * Creates a task with the given description and completion status.
     *
     * @param description description shown to the user
     * @param isDone whether the task is completed
     */
    public Task(String description, boolean isDone) {
        this.description = description;
        this.isDone = isDone;
    }

    /**
     * Returns the task description.
     *
     * @return task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Checks whether the task is completed.
     *
     * @return true when the task is completed
     */
    public boolean isDone() {
        return isDone;
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
     * Returns the marker displayed inside the task's status box.
     *
     * @return {@code X} when completed, or a space otherwise
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Formats the completion status and description shared by all task types.
     *
     * @return status icon followed by the task description
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
