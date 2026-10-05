package kaykay.model;

import kaykay.storage.Storage;

/**
 * Represents a task that can be marked as done or not done.
 *
 */
public class Task {
    /** The text describing what needs to be done. */
    private final String description;

    /** Whether this task has been completed. */
    private boolean isDone;

    /**
     * Creates an unfinished task with the given description.
     *
     * @param description what needs to be done.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the completion marker used in the task's display text.
     *
     * @return {@code "X"} when the task is done, otherwise a blank space.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /**
     * Returns the text used when searching for this task.
     *
     * @return the task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Checks whether this task has been completed.
     *
     * @return true if this task has been completed.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Checks whether another task has the same type and user-entered details.
     * Completion status is excluded because it is mutable task state.
     *
     * @param otherTask task to compare.
     * @return true if both tasks represent the same user-entered details.
     */
    public boolean hasSameDetails(Task otherTask) {
        return getClass().equals(otherTask.getClass())
                && description.equals(otherTask.description);
    }

    /** Marks this task as completed. */
    public void mark() {
        this.isDone = true;
    }

    /** Marks this task as not completed. */
    public void unmark() {
        this.isDone = false;
    }

    /**
     * Returns the task's user-facing display text.
     *
     * @return the task type-independent display text.
     */
    @Override
    public String toString() {
        return String.format("[%s] %s", this.getStatusIcon(), this.description);
    }

    /**
     * Returns the task in the format used by the task storage file.
     *
     * @return the serialized task.
     */
    public String toFileFormat() {
        return String.format("T | %d | %s", isDone ? 1 : 0, Storage.escape(description));
    }
}
