package nelson.task;

/**
 * Represents a task and whether it has been completed.
 */
public class Task {
    /** The text entered by the user for this task. */
    protected String description;
    /** Whether this task is complete. */
    protected boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description the text describing the task
     */
    public Task(String description) {
        assert description != null : "Task description must not be null";
        assert !description.isBlank() : "Task description must not be blank";
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the icon used to display this task's completion status.
     *
     * @return {@code "X"} when complete, otherwise a space
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns this task's description.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns this task in the format used for saving it to disk.
     *
     * @return the task type, status, and description separated by pipes
     */
    public String toStorageString() {
        return "T | " + (isDone ? "1" : "0") + " | " + description;
    }

    /** Marks this task as complete. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns this task in the basic status-and-description format.
     *
     * @return the task status and description
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
