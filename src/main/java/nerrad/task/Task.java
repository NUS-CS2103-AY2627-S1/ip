package nerrad.task;

/**
 * Represents a task with a description and completion status.
 */
public class Task {
    /** Description of the task. */
    protected String description;

    /** Whether the task has been completed. */
    protected boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not completed. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the character used to display this task's completion status.
     *
     * @return {@code X} when completed, or a space otherwise.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns whether this task has been marked as done.
     *
     * @return {@code true} if the task is done, otherwise {@code false}.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns this task's description.
     *
     * @return Task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the task in its user-facing display format.
     *
     * @return Status icon followed by the task description.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}

