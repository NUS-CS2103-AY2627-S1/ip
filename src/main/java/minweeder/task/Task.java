package minweeder.task;

import java.time.LocalDate;

/**
 * A single item to be tracked, with a description and a completion state.
 * Concrete subclasses define the task's specific type and how it is stored.
 */
public abstract class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates a task that is not yet done.
     *
     * @param description what the task is.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns a single-character icon representing this task's status.
     *
     * @return "X" if the task is done, otherwise a blank space.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /**
     * Returns the description of this task.
     *
     * @return the description of this task.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Marks this task as done.
     */
    public void mark() {
        this.isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void unmark() {
        this.isDone = false;
    }

    /**
     * Returns the representation of this task used when saving to the storage file.
     *
     * @return the file-format string for this task.
     */
    public abstract String toFileString();

    /**
     * Checks whether this task is considered to be occurring on the given date.
     * The default implementation returns false; subclasses with date fields override this.
     *
     * @param date the date to check against.
     * @return true if the task occurs on the given date.
     */
    public boolean isOccurringOn(LocalDate date) {
        return false;
    }

    /**
     * Returns the shared "done status | description" fields used by every task type's
     * {@link #toFileString()}, so subclasses only need to add their own extra fields.
     *
     * @return the common file-format fields for this task.
     */
    protected String toFileFields() {
        return (this.isDone ? "1" : "0") + " | " + this.description;
    }

    @Override
    public String toString() {
        return "[" + this.getStatusIcon() + "] " + this.description;
    }
}
