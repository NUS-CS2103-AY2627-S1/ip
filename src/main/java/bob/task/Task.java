package bob.task;

import java.time.LocalDateTime;

import bob.exception.BobException;

/**
 * Represents a general task in the task list.
 */
public abstract class Task {
    /**
     * Separates fields in the persistent storage representation.
     */
    public static final char STORAGE_DELIMITER = 0x01;

    /**
     * Stores the task description.
     */
    protected String name;

    /**
     * Indicates whether the task is completed.
     */
    protected boolean isDone;

    /**
     * Constructs a Task with the specified description.
     *
     * @param name the description of the task
     */
    public Task(String name) {
        assert name != null && !name.isBlank() : "Task description should not be null or blank";
        this.name = name;
        this.isDone = false;
    }

    /**
     * Returns the description name of the task.
     *
     * @return the description name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Checks whether the task is completed.
     *
     * @return true if task is done, false otherwise
     */
    public boolean isDone() {
        return this.isDone;
    }

    /**
     * Returns the done code for storage serialization.
     *
     * @return 1 if task is done, 0 otherwise
     */
    protected int getDoneCode() {
        return this.isDone ? 1 : 0;
    }

    /**
     * Returns the string representation of the task showing status icon and
     * description.
     *
     * @return string representation of this task
     */
    @Override
    public String toString() {
        return "[" + (this.isDone ? "X" : " ") + "] " + this.name;
    }

    /**
     * Checks if the task description contains the specified search keyword
     * (case-insensitive).
     *
     * @param keyword the substring to search for
     * @return true if the description contains the keyword, false otherwise
     */
    public boolean containsKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return false;
        }
        return this.name.toLowerCase().contains(keyword.toLowerCase());
    }

    /**
     * Marks the task as completed.
     */
    public void mark() {
        this.isDone = true;
        assert this.isDone : "Task should be marked as done";
    }

    /**
     * Marks the task as not completed.
     */
    public void unmark() {
        this.isDone = false;
        assert !this.isDone : "Task should be marked as not done";
    }

    /**
     * Formats the task as a string suitable for persistent storage export.
     *
     * @return the exported string representation of the task
     */
    public abstract String export();

    /**
     * Creates an updated copy of this task with the specified optional new field values.
     *
     * @param newName     the new description, or null if unchanged
     * @param newDeadline the new deadline date-time, or null if unchanged
     * @param newFrom     the new start date-time, or null if unchanged
     * @param newTo       the new end date-time, or null if unchanged
     * @return a new updated {@link Task} instance with completion status preserved
     * @throws BobException if an incompatible field is provided for this task type or validation fails
     */
    public abstract Task update(String newName, LocalDateTime newDeadline, LocalDateTime newFrom, LocalDateTime newTo)
            throws BobException;
}
