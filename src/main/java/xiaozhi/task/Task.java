package xiaozhi.task;

/**
 * Represents a task that XiaoZhi keeps track of.
 * <p>
 * A task has a description and a completion status, and can be marked
 * done/not done, printed for display, or converted into the plain-text
 * format used to save it to disk.
 */
public abstract class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Creates a task with the given description, initially not done.
     *
     * @param description What the task is.
     */
    public Task(String description) {
        assert description != null
                : "Task description should never be null; Parser rejects blank/missing descriptions "
                + "before a Task is constructed.";
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the one-character icon shown for this task's completion status.
     *
     * @return {@code "X"} if this task is done, a single space otherwise.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /**
     * Returns the single-letter code (e.g. "T", "D", "E") used to identify
     * this task's type when it is saved to disk.
     */
    public abstract String getTypeIcon();

    /**
     * Returns this task's description.
     *
     * @return The description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task is currently marked done.
     *
     * @return {@code true} if this task is done, {@code false} otherwise.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns this task encoded as one line of the save-file format:
     * {@code <type> | <isDone> | <description>}. Subclasses append any
     * extra fields they have (e.g. a deadline's date) after this.
     *
     * @return The task encoded for storage on disk.
     */
    public String toSaveFormat() {
        return getTypeIcon() + " | " + (isDone ? "1" : "0") + " | " + description;
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
