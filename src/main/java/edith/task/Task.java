package edith.task;

import java.time.LocalDateTime;
import java.util.Locale;

/**
 * Represents a task that Edith can track and mark as complete.
 */
public class Task {
    /** The description supplied by the user. */
    protected String description;

    /** The current completion status of this task. */
    protected TaskStatus status;

    private long insertionOrder = -1;

    /**
     * Creates a task that is initially not done.
     *
     * @param description the task description
     */
    public Task(String description) {
        this.description = description;
        this.status = TaskStatus.NOT_DONE;
    }

    /**
     * Returns the icon that represents this task's completion status.
     *
     * @return {@code X} for a completed task, otherwise a space
     */
    public String getStatusIcon() {
        return status.getIcon();
    }

    /** Returns the task description. */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the task's relevant date for sorting, or null if it has no sortable date.
     *
     * @return date used for sorting, or null when no sortable date is available
     */
    public LocalDateTime getDateTimeForSorting() {
        return null;
    }

    /** Returns whether this task is complete. */
    public boolean isDone() {
        return status == TaskStatus.DONE;
    }

    /** Returns the task's permanent insertion order. */
    public long getInsertionOrder() {
        return insertionOrder;
    }

    /** Assigns the task's permanent insertion order. */
    void setInsertionOrder(long insertionOrder) {
        assert insertionOrder >= 0 : "Insertion order must not be negative";
        this.insertionOrder = insertionOrder;
    }

    /** Marks this task as complete. */
    public void markAsDone() {
        status = TaskStatus.DONE;
    }

    /** Marks this task as not yet complete. */
    public void unmarkAsDone() {
        status = TaskStatus.NOT_DONE;
    }

    /**
     * Returns whether this task description contains the given keyword,
     * ignoring letter case.
     *
     * @param keyword text to search for
     * @return true when the description contains the keyword
     */
    public boolean matchesDescription(String keyword) {
        return description.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

    /**
     * Returns whether another task has the same user-visible details as this task.
     *
     * @param other task to compare
     * @return true when the task type and description are the same, ignoring case
     */
    public boolean hasSameDetails(Task other) {
        return other != null
                && getClass().equals(other.getClass())
                && normalize(description).equals(normalize(other.description));
    }

    /**
     * Returns a simple representation suitable for saving to disk.
     *
     * @return task type, completion status, and description
     */
    public String toFileFormat() {
        return getTypeCode() + " | " + (status == TaskStatus.DONE ? "1" : "0")
                + " | " + description;
    }

    /**
     * Returns the type code used in the save file.
     *
     * @return the task type code
     */
    protected String getTypeCode() {
        return "T";
    }

    /** Returns a comparison value that ignores insignificant letter case and spacing. */
    protected static String normalize(String text) {
        return text.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    /**
     * Returns this task in the format used when displaying a task list.
     *
     * @return the status icon and task description
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
