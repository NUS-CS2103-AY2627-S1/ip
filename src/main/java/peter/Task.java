package peter;

import java.util.Objects;

/**
 * Represents a task in Peter's task list.
 */
public class Task {
    public static final String DONE_MARKER = "1";
    public static final String NOT_DONE_MARKER = "0";
    protected String description;
    protected boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description the task description
     */
    public Task(String description) {
        assert description != null : "task description should not be null";
        this.description = description;
        this.isDone = false;
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the display icon for this task's completion state.
     *
     * @return {@code [X]} when complete, otherwise {@code [ ]}
     */
    public String getStatusIcon() {
        return isDone ? "[X]" : "[ ]";
    }

    /**
     * Returns this task's description.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns this task in the format used by Peter's storage file.
     *
     * @return the task's pipe-delimited storage representation
     */
    public String toFileFormat() {
        return "T | " + (isDone ? DONE_MARKER : NOT_DONE_MARKER) + " | " + description;
    }

    /**
     * Returns this task in Peter's normal display format.
     *
     * @return the formatted task
     */
    @Override
    public String toString() {
        return getStatusIcon() + " " + description;
    }

    /**
     * Compares tasks by type and description, ignoring completion state, so
     * that marking a task done does not change what counts as a duplicate of
     * it. Subclasses with extra fields (e.g. a deadline's due time) override
     * this to also compare those fields.
     *
     * @param other the object to compare against
     * @return true if {@code other} is a task of the same concrete type with an equal description
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Task otherTask = (Task) other;
        return description.equals(otherTask.description);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(getClass(), description);
    }
}
