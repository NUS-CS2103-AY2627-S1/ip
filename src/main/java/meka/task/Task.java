package meka.task;

import java.util.Locale;

/**
 * Represents a task and whether it has been completed.
 */
public class Task {
    /** Description entered by the user for this task. */
    private final String description;

    /** Whether this task has been marked as completed. */
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description description of the task.
     */
    public Task(String description) {
        assert description != null : "A task should have a description";
        assert !description.isBlank() : "A task description should contain visible characters";
        assert !description.contains(" | ") : "A task description should be safe to persist";

        this.description = description;
        this.isDone = false;
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Returns the symbol used to show whether this task is completed.
     *
     * @return {@code "X"} if the task is completed, or a space otherwise.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns whether this task's description contains the given keyword,
     * ignoring letter case.
     *
     * @param keyword keyword to search for.
     * @return true if the description contains the keyword.
     */
    public boolean containsKeyword(String keyword) {
        return description.toLowerCase(Locale.ENGLISH)
                .contains(keyword.toLowerCase(Locale.ENGLISH));
    }

    /**
     * Marks this task as incomplete.
     */
    public void unmark() {
        this.isDone = false;
    }

    /**
     * Returns a representation of this task suitable for saving to a file.
     *
     * @return the task type, completion status, and description.
     */
    public String toDataString() {
        return formatDataString("T");
    }

    /**
     * Formats the common fields shared by all saved task types.
     *
     * @param taskType letter identifying the task type.
     * @return the task type, completion status, and description.
     */
    protected String formatDataString(String taskType) {
        return taskType + " | " + (isDone ? "1" : "0") + " | " + description;
    }

    /**
     * Returns the task with its completion status for display.
     *
     * @return a task formatted as "[X] description" or "[ ] description".
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
