package shai.task;

/**
 * Represents a task with a description and a completion status.
 */
public class Task {
    /** The text describing this task. */
    protected String description;

    /** Whether this task has been marked as done. */
    protected boolean isDone;

    /**
     * Creates a new task that is initially not done.
     *
     * @param description the text describing the task
     */
    public Task(String description) {
        if (description == null) {
            throw new IllegalArgumentException("Task description must not be null.");
        }
        this.description = description;
        this.isDone = false;
    }

    /**
     * Gets the text describing this task.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets whether this task has been completed.
     *
     * @return {@code true} if the task is done
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Gets the symbol representing this task's completion status.
     *
     * @return {@code X} if the task is done, otherwise a blank space
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /** Marks this task as done. */
    public void markAsDone() {
        this.isDone = true;
    }

    /** Marks this task as not done. */
    public void unmark() {
        this.isDone = false;
    }

    /**
     * Returns the task in the format used when displaying it.
     *
     * @return the status icon followed by the task description
     */
    @Override
    public String toString() {
        return "[" + this.getStatusIcon() + "] " + this.description;
    }
}
