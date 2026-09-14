package johnny.task;

/**
 * Represents a task with a description and completion status.
 * Base class for Todo, Deadline, and Event.
 */
public class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Creates a new task with the given description, initially not done.
     *
     * @param description the task description
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /** Returns "X" if the task is done, or a space " " if not done. */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /** Returns whether this task is completed. */
    public boolean isDone() {
        return isDone;
    }

    public void markAsDone() {
        this.isDone = true;
    }

    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns a pipe-delimited string for saving this task to a file.
     * Subclasses override this to include their specific fields.
     */
    public String toFileString() {
        return (isDone ? "1" : "0") + " | " + description;
    }

    /** Returns a display string in the format {@code [X] description} or {@code [ ] description}. */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
