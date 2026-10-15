package bo.model;

/**
 * Represents a task tracked by Bo.
 */
public class Task {
    /** The text describing this task. */
    protected String description;

    /** Whether this task has been marked as done. */
    protected boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description the text describing the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the task description.
     *
     * @return the text describing this task.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task has been marked as done.
     *
     * @return {@code true} when this task is done.
     */
    public boolean isDone() {
        return isDone;
    }

    /** Marks this task as done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void unmarkAsDone() {
        isDone = false;
    }

    /**
     * Returns the symbol used to display this task's completion status.
     *
     * @return {@code X} when done, or a space when not done.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the single-letter type icon shown before this task.
     *
     * @return the task type icon.
     */
    public String getTypeIcon() {
        return "";
    }

    /**
     * Returns the task in the format used by Bo's task list.
     *
     * @return the type icon, status icon, and task description.
     */
    @Override
    public String toString() {
        if (getTypeIcon().isEmpty()) {
            return "[" + getStatusIcon() + "] " + description;
        }
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description;
    }
}
