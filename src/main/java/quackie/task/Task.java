package quackie.task;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task entered during the current Quackie session.
 */
public class Task {
    /**
     * Shared user-facing format for structured date-times, so deadlines and events display them identically
     * (e.g. "Sep 24 2026, 2:00 PM").
     */
    static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a", Locale.ENGLISH);

    /** The user-facing text describing this task. */
    protected String description;

    /** Whether this task has been completed. */
    protected boolean isDone;

    /**
     * Creates a task that is initially not done.
     *
     * @param description the text describing the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /** Marks this task as done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void markAsUndone() {
        isDone = false;
    }

    /**
     * Returns the task description.
     *
     * @return the text describing this task
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task is done.
     *
     * @return {@code true} if this task is done
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the status icon used when displaying this task.
     *
     * @return {@code X} for a done task, or a blank space otherwise
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the icon identifying this task type.
     *
     * @return the task type icon, or an empty string for a generic task
     */
    protected String getTypeIcon() {
        return "";
    }

    /**
     * Checks whether another task has the same type and user-entered details.
     * Completion status is deliberately ignored when detecting duplicates.
     *
     * @param other task to compare with this task
     * @return {@code true} when both tasks describe the same work
     */
    boolean hasSameDetails(Task other) {
        return other != null
                && getClass().equals(other.getClass())
                && description.equalsIgnoreCase(other.description);
    }

    @Override
    public String toString() {
        return getTypeIcon() + "[" + getStatusIcon() + "] " + description;
    }
}
