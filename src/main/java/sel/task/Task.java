package sel.task;

/**
 * Represents a task with a description and a done/not-done status.
 * Serves as the base class for {@link ToDo}, {@link Deadline}, and
 * {@link Event}.
 */
public class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Creates a new, not-done task with the given description.
     *
     * @param description text describing the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns a single-character status icon: {@code "X"} if the task is
     * done, or a blank space otherwise.
     *
     * @return the status icon for this task.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /**
     * Returns the description of this task.
     *
     * @return the task's description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns the description of this task. Subclasses override this to
     * include type markers and status icons in their display form.
     *
     * @return the description of this task.
     */
    public String toString() {
        return this.description;
    }

    /**
     * Returns whether this task has been marked as done.
     *
     * @return {@code true} if the task is done, {@code false} otherwise.
     */
    public boolean isDone() {
        return this.isDone;
    }

    /**
     * Checks whether another task describes the same thing as this one.
     *
     * <p>Used to stop the same task being added twice. Two tasks match
     * only if they are of the same kind and their descriptions are equal
     * ignoring case, so "read book" and "Read Book" count as duplicates.
     * Subclasses widen this to include their dates.
     *
     * <p>This is deliberately a named method rather than an override of
     * {@code equals}: task lists are indexed by position, and redefining
     * equality would change how {@code List.remove}, {@code contains} and
     * friends behave.
     *
     * @param other the task to compare against, possibly {@code null}.
     * @return {@code true} if both tasks describe the same thing.
     */
    public boolean hasSameDetailsAs(Task other) {
        return other != null
                && this.getClass() == other.getClass()
                && this.description.equalsIgnoreCase(other.description);
    }

    /** Marks this task as done. */
    public void mark() {
        this.isDone = true;
    }

    /** Marks this task as not done. */
    public void unmark() {
        this.isDone = false;
    }
}
