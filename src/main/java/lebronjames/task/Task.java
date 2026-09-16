package lebronjames.task;

import java.time.LocalDate;

/**
 * Represents a task and whether it has been completed.
 *
 * <p>This class is abstract because every task on the list is really a
 * {@link Todo}, {@link Deadline}, or {@link Event}; only those subclasses know
 * how to render themselves for display and for storage on disk.
 */
public abstract class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        // Parser and Storage each reject an empty description before building a
        // task, so a blank one arriving here is not bad user input; it means a
        // new construction path has been added that skipped those checks.
        assert description != null && !description.isBlank() : "A task must have a non-blank description";

        this.description = description;
        this.isDone = false;
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the symbol used to display this task's completion status.
     *
     * @return {@code X} if completed, or a space if incomplete.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the description of this task.
     *
     * @return Description supplied when the task was created.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task happens on the given date.
     *
     * <p>A plain {@link Todo} has no date at all, so the default answer is no.
     * {@link Deadline} and {@link Event} override this to compare against the
     * dates they carry.
     *
     * @param date Date the user is asking about.
     * @return Whether this task falls on that date.
     */
    public boolean isOn(LocalDate date) {
        return false;
    }

    /**
     * Returns the completion status encoded for the save file.
     *
     * @return {@code 1} if completed, or {@code 0} if incomplete.
     */
    protected String getDoneFlag() {
        return isDone ? "1" : "0";
    }

    /**
     * Returns this task encoded as a single line of the save file.
     *
     * <p>Fields are separated by {@code " | "}, for example
     * {@code "D | 0 | return book | 2019-06-06"}.
     *
     * @return Save-file representation of this task.
     */
    public abstract String toFileFormat();

    /**
     * Returns whether the given object is the same task as this one.
     *
     * <p>Two tasks are the same when they are of the same kind and carry the
     * same description, ignoring capitalisation, so that adding "read book"
     * twice is recognised however it was typed. Subclasses that carry dates
     * extend this to compare those too.
     *
     * <p>Completion status is deliberately left out. A task the user has
     * already ticked off is still the same task, so marking one done must not
     * make room for a second copy of it.
     *
     * @param other Object to compare with.
     * @return Whether it is a task of the same kind with the same description.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        // getClass rather than instanceof: a Todo and a Deadline that happen to
        // share a description are different tasks, not duplicates.
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        Task otherTask = (Task) other;
        return description.equalsIgnoreCase(otherTask.description);
    }

    @Override
    public int hashCode() {
        // Lower-cased to stay consistent with the case-insensitive equals above:
        // two objects that are equal must never have different hash codes.
        return description.toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
