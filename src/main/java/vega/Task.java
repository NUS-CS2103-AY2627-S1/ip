package vega;

/**
 * Represents a task in Vega's list. Subclasses provide the task-specific
 * details while this class keeps the common description and completion state.
 */
public class Task {
    private final String description;

    private final String type;

    private boolean isDone;

    /**
     * Creates a task with a description and a one-letter task type.
     *
     * @param description Description of the task.
     * @param type One-letter task type used when displaying and saving the task.
     */
    public Task(String description, String type) {
        assert description != null : "Task description must not be null";
        assert type != null && type.length() == 1 : "Task type must be a one-letter code";
        this.description = description;
        this.type = type;
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
     * Returns this task's description for persistent storage.
     *
     * @return Description of this task.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the one-letter type code used for this task.
     *
     * @return One-letter task type.
     */
    public String getType() {
        return type;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return {@code true} if this task is complete.
     */
    public boolean isDone() {
        return isDone;
    }

    @Override
    public String toString() {
        String status = isDone ? "X" : " ";
        return "[" + type + "][" + status + "] " + description;
    }
}
