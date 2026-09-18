package fein.task;

/** Represents a task in Fein's task list. */
public class Task {
    /** The text describing this task. */
    protected String description;

    /** Whether this task has been completed. */
    protected boolean isDone;

    /** Creates an incomplete task with the given description. */
    public Task(String description) {
        // All task types rely on a description for display, searching, and persistence.
        assert description != null && !description.isBlank() : "A task must have a non-blank description";
        this.description = description;
        this.isDone = false;
    }

    /** Returns the icon used to show whether this task is done. */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Marks this task as done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void markAsNotDone() {
        isDone = false;
    }

    /** Returns the task description. */
    public String getDescription() {
        return description;
    }

    /** Returns whether this task has been completed. */
    public boolean isDone() {
        return isDone;
    }

    /** Returns whether another task has the same type and user-provided details as this task. */
    public boolean hasSameDetailsAs(Task other) {
        // Duplicate comparisons require two actual task instances.
        assert other != null : "A duplicate comparison requires another task";
        return getClass().equals(other.getClass())
                && description.equalsIgnoreCase(other.description);
    }

    /** Returns the common status-and-description representation of this task. */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
