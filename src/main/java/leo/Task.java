package leo;

/**
 * Represents a task that Leo is tracking. A task has a description and
 * a done/not-done status. Subclasses (e.g. {@link Deadline},
 * {@link Event}) add their own extra details on top of this.
 */
public class Task {
    private String description;
    private boolean isDone;

    /**
     * Creates a new, not-done task with the given description.
     *
     * @param description description of the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
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
     * Returns a one-character status icon: "X" if done, otherwise a
     * single space.
     *
     * @return the status icon
     */
    public String getStatus() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the one-letter type code for this task, used in both the
     * console display and the save-file format. Subclasses override
     * this to return their own type letter (e.g. "D" for Deadline).
     *
     * @return the type code, "T" for a plain Task/Todo
     */
    public String getType() {
        return "T";
    }

    /**
     * Returns the description of this task.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task is marked as done.
     *
     * @return true if done, false otherwise
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns this task encoded as a single line for the save file, in
     * the format "TYPE | DONE_FLAG | DESCRIPTION" (subclasses append
     * their own extra fields after this).
     *
     * @return the save-file line for this task
     */
    public String toFileString() {
        return getType() + " | "
                + (isDone ? "1" : "0") + " | "
                + description;
    }

    /**
     * Returns this task formatted for display to the user, e.g.
     * "[T][X] read book".
     *
     * @return the display string for this task
     */
    @Override
    public String toString() {
        return "[" + getType() + "][" + getStatus()
                + "] " + description;
    }

    /**
     * Checks whether this task represents the same task as another -
     * same type, description, and any type-specific fields a subclass
     * adds (e.g. a Deadline's due date) - regardless of done status.
     * Used to warn the user when they add what looks like a duplicate
     * entry.
     *
     * <p>Compares via {@link #toFileString()} rather than field-by-field,
     * so a subclass that appends its own fields to that string (every
     * subclass already does, to round-trip through the save file) is
     * compared correctly without needing to override this method
     * itself. The type code and done flag are always exactly one
     * character each (see {@link #toFileString()}), so
     * {@code "X | Y | "} is always 8 characters - skipping it compares
     * everything after the done flag, i.e. every field that actually
     * distinguishes one task from another.
     *
     * @param other task to compare against
     * @return true if other represents the same task as this one
     */
    public boolean isSameTaskAs(Task other) {
        return other != null
                && toFileString().substring(8).equals(other.toFileString().substring(8));
    }
}
