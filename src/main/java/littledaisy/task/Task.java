package littledaisy.task;

/**
 * A single item on littleDaisy's task list.
 *
 * <p>A task is a line of text plus a flag saying whether it has been done.
 */
public class Task {
    /** What the user typed when adding this task. */
    private final String description;

    /** Whether the user has marked this task as done. */
    private boolean isDone;

    /**
     * Creates a task that is not done yet.
     *
     * @param description what the task is about
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /** Records that this task has been done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Records that this task is not done after all. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the user-entered task description.
     *
     * @return task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return {@code true} if the task is marked as done
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns whether another task has the same type and user-entered details.
     * Completion status is deliberately ignored when checking duplicates.
     *
     * @param other task to compare
     * @return {@code true} when both tasks represent the same work
     */
    public boolean hasSameDetails(Task other) {
        return other != null
                && getClass().equals(other.getClass())
                && description.equalsIgnoreCase(other.description);
    }

    /**
     * Returns the one-character mark shown inside the status box.
     *
     * @return {@code "X"} if this task is done, a single space otherwise
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns this task as it should appear to the user, e.g. {@code [X] read
     * book}.
     *
     * <p>Level-4 subclasses override this to put a type icon in front, reusing
     * this result via {@code super.toString()}.
     *
     * @return the status box followed by the description
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
