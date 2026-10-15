package elora.task;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a task with a description and a done/not-done status.
 * Serves as the base class for Todo, Deadline, and Event.
 */
public class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Creates a new, not-done task with the given description.
     *
     * @param description What the task is about.
     */
    public Task(String description) {
        assert description != null && !description.isEmpty()
                : "callers (Elora) should have already rejected null/empty descriptions before construction";
        this.description = description;
        this.isDone = false;
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns the single-character icon representing this task's status.
     *
     * @return "X" if the task is done, or a single space otherwise.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /**
     * Returns this task's description.
     *
     * @return The description given when this task was created.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task occurs on the given date. A plain Task
     * has no associated date, so this always returns false; subclasses
     * with a meaningful date (e.g. Deadline) override this.
     *
     * @param date The date to check against.
     * @return false, always, for a plain Task.
     */
    public boolean isOccurringOn(LocalDate date) {
        return false;
    }

    /**
     * Returns the date used to sort this task chronologically against
     * others, or null if this task has no single associated date. A
     * plain Task has none; Deadline overrides this with its due date.
     *
     * @return This task's sort date, or null if it doesn't have one.
     */
    public LocalDate getSortDate() {
        return null;
    }

    /**
     * Returns this task's representation for the save file, as
     * "&lt;0 or 1&gt; | &lt;description&gt;". Subclasses prepend their
     * type letter and append any extra fields of their own.
     *
     * @return The save-file line for this task, without its type prefix.
     */
    public String toSaveFormat() {
        return (isDone ? "1" : "0") + " | " + description;
    }

    /**
     * Returns this task as "[&lt;status icon&gt;] &lt;description&gt;".
     * Subclasses prepend their own type marker to this.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }

    /**
     * Returns whether this task has the same type and description as
     * another, ignoring done/not-done status. Used to detect duplicate
     * tasks when a new one is added. Subclasses with extra fields (e.g.
     * Deadline's due date) override this to also compare those fields.
     *
     * @param other The object to compare against.
     * @return true if {@code other} is a task of the same concrete type
     *     with the same description.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Task that = (Task) other;
        return description.equals(that.description);
    }

    /**
     * Returns a hash code consistent with {@link #equals(Object)}: equal
     * tasks (same concrete type and description) always hash the same.
     *
     * @return This task's hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(getClass(), description);
    }
}
