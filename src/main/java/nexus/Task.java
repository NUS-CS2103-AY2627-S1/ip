package nexus;

/**
 * Represents a task that can be marked as completed.
 */
public class Task {
    protected final String description;
    private final TaskType type;
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description text describing the task.
     * @param type category of this task.
     */
    public Task(String description, TaskType type) {
        this.description = description;
        this.type = type;
        this.isDone = false;
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void unmark() {
        isDone = false;
    }

    /** Returns whether this task has been completed. */
    public boolean isDone() {
        return isDone;
    }

    /** Returns the text that describes this task. */
    public String getDescription() {
        return description;
    }

    /** Returns the category of this task. */
    public TaskType getType() {
        return type;
    }

    /**
     * Returns the display form of this task.
     *
     * @return the task status followed by its description
     */
    @Override
    public String toString() {
        return "[" + type.getSymbol() + "][" + (isDone ? "X" : " ") + "] " + description;
    }
}
