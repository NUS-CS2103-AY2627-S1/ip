package sylveon;

/**
 * Represents one task in sylveon.Sylveon's task list.
 */
public class Task {
    private final String description;
    private boolean isDone;

    /** Creates a task that is initially not done. */
    public Task(String description) {
        assert description != null : "Task description must not be null";
        this.description = description;
        this.isDone = false;
    }

    /** Marks this task as done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the task description.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return true if the task is done, otherwise false
     */
    public boolean isDone() {
        return isDone;
    }

    /** Returns the task's display status and description. */
    @Override
    public String toString() {
        return "[" + (isDone ? "X" : " ") + "] " + description;
    }
}
