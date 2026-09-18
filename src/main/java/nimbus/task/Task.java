package nimbus.task;

/**
 * Represents a task and its completion status.
 */
public class Task {
    protected String description;
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        this.description = description;
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void markAsNotDone() {
        isDone = false;
    }

    /** Returns the icon that represents this task's completion status. */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Returns whether this task is completed. */
    public boolean isDone() {
        return isDone;
    }

    /** Returns the task description. */
    public String getDescription() {
        return description;
    }

    /** Replaces the task description. */
    public void setDescription(String description) {
        assert description != null : "Task description must not be null";
        assert !description.isBlank() : "Task description must not be blank";
        this.description = description;
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
