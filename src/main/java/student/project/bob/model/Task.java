package student.project.bob.model;

/**
 * Represents a task that can be marked as done or undone.
 */
public class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description description of the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the symbol used to show whether this task is complete.
     *
     * @return X for a completed task, or a blank space otherwise
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return true if this task is done
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the task in the format used by the list command.
     *
     * @return the task's status and description
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }

    /**
     * Returns the task description for confirmation messages.
     *
     * @return the description of this task
     */
    public String getDescription() {
        return description;
    }

    /**
     * Marks this task as completed.
     */
    public void setDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void setUndone() {
        this.isDone = false;
    }
}
