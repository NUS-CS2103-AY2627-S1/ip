package ralph.model;

/**
 * Represents a simple task with a description and done status.
 */
public class Task {
    /**
     * The task description.
     */
    protected String description;
    /**
     * Whether the task is done.
     */
    protected boolean isDone;

    /**
     * Constructs a Task with the given description; initially not done.
     *
     * @param description the task description
     */
    public Task(String description) {
        assert description != null : "Task description must not be null";
        assert !description.trim().isEmpty() : "Task description must not be blank";
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the textual status icon for the task when printed.
     *
     * @return "X" when done; a single space when not done
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /**
     * Returns the task description.
     *
     * @return the description text
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether the task is marked as done.
     *
     * @return true if done, false otherwise
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Marks the task as done.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks the task as not done.
     */
    public void markAsNotDone() {
        this.isDone = false;
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
