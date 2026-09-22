package longfrog.task;

import java.time.LocalDate;
import java.util.Locale;

/** Represents a task with a name and completion state. */
public abstract class Task {
    protected String name;
    protected boolean isDone;

    /**
     * Creates an incomplete task with the given name.
     *
     * @param name the task description
     */
    public Task(String name) {
        this.name = name;
        this.isDone = false;
    }

    /**
     * Returns the task description.
     *
     * @return the task description
     */
    public String getName() {
        return this.name;
    }

    /**
     * Checks whether another task has the same duplicate identity as this task.
     *
     * <p>Task type and normalized description form the common identity. Concrete task types add
     * their date-time fields where applicable. Completion state is deliberately ignored.</p>
     *
     * @param other the task to compare with
     * @return whether the tasks have the same duplicate identity
     */
    public boolean isDuplicateOf(Task other) {
        return other != null
                && getClass().equals(other.getClass())
                && normalizeDescription(name).equals(normalizeDescription(other.name));
    }

    /**
     * Returns the symbol used to show this task's completion state.
     *
     * @return {@code "X"} when complete, otherwise a space
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /**
     * Returns whether this task is complete.
     *
     * @return whether the task is marked done
     */
    public boolean isDone() {
        return isDone;
    }

    /** Marks this task as complete. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void unmarkAsDone() {
        isDone = false;
    }

    /**
     * Checks whether this task occurs on a date.
     *
     * <p>Tasks without date information do not occur on any specific date.</p>
     *
     * @param date the date to check
     * @return whether this task occurs on the date
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns the completion state and description used by concrete task displays.
     *
     * @return the formatted completion state and task description
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + this.name;
    }

    /**
     * Formats the task as a delimited string for saving to disk.
     */
    public abstract String toFileFormat();

    /**
     * Normalizes a description solely for duplicate comparison.
     *
     * @param description the task description to normalize
     * @return the normalized description
     */
    private static String normalizeDescription(String description) {
        return description.strip()
                .replaceAll("\\p{javaWhitespace}+", " ")
                .toLowerCase(Locale.ROOT);
    }
}
