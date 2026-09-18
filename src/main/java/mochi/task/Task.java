package mochi.task;

import java.util.Locale;

/**
 * Represents a task with a description and completion status.
 */
public class Task {
    protected static final String STORAGE_SEPARATOR = " | ";

    protected String description;
    protected boolean isDone;

    /**
     * Creates a task that has not been completed.
     *
     * @param description description of the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Copies the common task details so pending changes can be saved safely.
     *
     * @param source task to copy
     */
    protected Task(Task source) {
        this.description = source.description;
        this.isDone = source.isDone;
    }

    /**
     * Returns an independent task with the same details and completion status.
     *
     * @return editable copy of this task
     */
    public Task copy() {
        return new Task(this);
    }

    /**
     * Returns the description without status or type decorations.
     *
     * @return task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the symbol used to display the completion status.
     *
     * @return {@code X} when done, otherwise a space
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not completed.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Checks whether this task's description contains a keyword, ignoring letter case.
     *
     * @param keyword text to search for
     * @return true when the description contains the keyword
     */
    public boolean containsKeyword(String keyword) {
        return description.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

    /**
     * Returns the completion status in the storage format.
     *
     * @return {@code 1} when done, otherwise {@code 0}
     */
    protected String getStorageStatus() {
        return isDone ? "1" : "0";
    }

    /**
     * Converts this task into a line suitable for persistent storage.
     *
     * @return storage representation of this task
     */
    public String toFileString() {
        return getStorageStatus() + STORAGE_SEPARATOR + description;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
