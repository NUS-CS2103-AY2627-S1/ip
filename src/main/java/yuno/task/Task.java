package yuno.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a task with a description and completion status.
 */
public abstract class Task {
    /** Stores the text that describes this task. */
    private final String description;
    /** Records whether this task has been completed. */
    private boolean isDone;

    /**
     * Creates a task with the specified description and completion status.
     *
     * @param description Text that describes the task.
     * @param isDone Whether the task is completed.
     */
    public Task(String description, boolean isDone) {
        assert description != null : "Task description must not be null";
        assert !description.isBlank() : "Task description must not be blank";
        this.description = description;
        this.isDone = isDone;
    }

    /**
     * Returns the text that describes this task.
     *
     * @return Task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the symbol representing this task's completion status.
     *
     * @return {@code X} if completed; otherwise, a space.
     */
    public char getStatus() {
        return (isDone ? 'X' : ' ');
    }

    /**
     * Marks this task as completed.
     */
    public void markDone() {
        isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void unmarkDone() {
        isDone = false;
    }

    /**
     * Returns whether this task is relevant for the specified date.
     *
     * @param date Date to check.
     * @return True if this task is relevant for the date; otherwise, false.
     */
    public abstract boolean isRelevantFor(LocalDate date);

    /**
     * Returns whether this task's description contains the specified lowercase text.
     *
     * @param searchText Lowercase text to find within the task description.
     * @return True if the task description contains the text; otherwise, false.
     */
    public boolean containsText(String searchText) {
        return getDescription().toLowerCase().contains(searchText);
    }

    /**
     * Returns this task in the format used by the task data file.
     *
     * @return Storage-ready task data.
     */
    public abstract String toStorageString();

    /**
     * Returns the date and time used to place this task in chronological order.
     * Undated tasks return the latest representable date-time so they sort after dated tasks.
     *
     * @return Date and time used for chronological sorting.
     */
    public abstract LocalDateTime getChronologicalTime();

    /**
     * Returns an independent copy of this task.
     *
     * @return Copy containing the same task data.
     */
    abstract Task copy();

    /**
     * Returns this task in a display-ready format with its completion status.
     *
     * @return Formatted task description and status.
     */
    @Override
    public String toString() {
        return String.format("[%c] %s", getStatus(), getDescription());
    }
}
