package lion;

/**
 * Defines the shared description and completion state of every task type.
 */
public abstract class Task {
    /** Human-readable details describing the task. */
    protected String description;
    /** Whether the task has been completed. */
    protected boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description description of the task.
     */
    public Task(String description) {
        // A null description would only surface later as a NullPointerException
        // in toString() or getDescription(), far from where it was introduced.
        assert description != null : "task description must not be null";

        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the storage-friendly completion indicator.
     *
     * @return {@code 1} when complete, otherwise {@code 0}.
     */
    public String getStatusIcon() {
        return (isDone ? "1" : "0");
    }

    /**
     * Returns the task description.
     *
     * @return task description.
     */
    public String getDescription() {
        return this.description;
    }

    /** Marks this task as complete. */
    public void markAsDone() {
        this.isDone = true;
    }

    /** Marks this task as incomplete. */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns the one-letter icon identifying the concrete task type.
     *
     * @return task type icon.
     */
    public abstract String getTypeIcon();

    /**
     * Returns whether another task has the same type and description as this one, used
     * to flag likely-duplicate tasks when adding a new one. {@link Deadline} and
     * {@link Event} extend this to also compare their date fields.
     *
     * @param other task to compare against.
     * @return true if the two tasks share the same type and description.
     */
    public boolean hasSameDetails(Task other) {
        return other != null
                && getClass() == other.getClass()
                && description.equals(other.description);
    }

    /**
     * Returns the display-friendly completion checkbox.
     *
     * @return {@code [X]} when complete, otherwise {@code [ ]}.
     */
    private String getStatusBox() {
        return isDone ? "[X]" : "[ ]";
    }

    /**
     * Returns the task's display-friendly base representation.
     *
     * @return type icon, completion checkbox, and description, e.g. {@code [T] [X] read book}.
     */
    @Override
    public String toString() {
        return "[" + getTypeIcon() + "] " + getStatusBox() + " " + description;
    }
}
