package atom.task;

/**
 * A parent class for tasks.
 */
public abstract class Task {
    /** Indicates whether the task is done */
    protected boolean isDone;

    /** The task description */
    protected final String description;

    /**
     * Instantiates a <code>Task</code>.
     *
     * @param description The task description.
     */
    public Task(String description) {
        this.description = description;
        isDone = false;
    }

    /**
     * Marks a task as done.
     */
    public void mark() {
        isDone = true;
    }

    /**
     * Marks a task as undone.
     */
    public void unmark() {
        isDone = false;
    }

    /**
     * @return The description of the task.
     */
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        String cross = isDone ? "X" : " ";
        return String.format("[%s] %s", cross, description);
    }
}
