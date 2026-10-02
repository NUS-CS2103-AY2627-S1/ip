package clsl.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task with a description and completion status.
 */
public class Task {
    protected static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy");

    private boolean isDone;
    private final String name;

    /**
     * Creates an incomplete task with the specified description.
     *
     * @param name Description of the task.
     */
    public Task(String name) {
        this.name = name;
        this.isDone = false;
    }

    /**
     * Returns the icon representing this task's completion status.
     *
     * @return {@code [X]} when complete, or {@code [ ]} otherwise.
     */
    public String getStatusIcon() {
        return isDone ? "[X]" : "[ ]";
    }

    /** Marks this task as complete. */
    public void markAsDone() {
        this.isDone = true;
    }

    /** Marks this task as incomplete. */
    public void unmarkAsDone() {
        this.isDone = false;
    }

    /**
     * Returns this task's description.
     *
     * @return The task description.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns whether this task is a duplicate of the specified task.
     *
     * @param other Task to compare with this task.
     * @return {@code true} if both tasks have the same type-specific details, or {@code false}
     *         otherwise.
     */
    public boolean isDuplicateOf(Task other) {
        return getClass().equals(other.getClass())
                && normalizeName(name).equals(normalizeName(other.name));
    }

    /**
     * Returns a task description in the format used for duplicate comparison.
     *
     * @param description Description to normalize.
     * @return A lowercase description with whitespace removed.
     */
    protected static String normalizeName(String description) {
        return description.replaceAll("\\s+", "").toLowerCase(Locale.ROOT);
    }

    /**
     * Returns this task in its display format.
     *
     * @return The completion icon followed by the description.
     */
    @Override
    public String toString() {
        return getStatusIcon() + " " + name;
    }

    /**
     * Returns this task's completion status and description in the save-file format.
     *
     * @return Task completion status and description.
     */
    public String getSaveString() {
        String status = isDone ? "1" : "0";
        return status + " | " + name;
    }

    /**
     * Returns whether this task occurs on the given date.
     * The base implementation returns {@code false} because a plain task has no date.
     *
     * @param date The date to check.
     * @return {@code true} if the task occurs on the given date, or {@code false} otherwise.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }
}
