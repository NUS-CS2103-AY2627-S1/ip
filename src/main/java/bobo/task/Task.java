package bobo.task;

import java.time.LocalDate;

/**
 * Represents a generic task in Bobo.
 */
public class Task extends MemoryItem {
    protected String description;
    protected boolean isDone;

    /**
     * Constructs a Task with the specified description.
     *
     * @param description Task description.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the status icon for this task ("X" if done, " " if not done).
     *
     * @return Status icon string.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as not completed yet.
     */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns the description of the task.
     *
     * @return Task description string.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether the task is done.
     *
     * @return true if done, false otherwise.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Checks if this task occurs on the specified date.
     *
     * @param targetDate The date to check against.
     * @return true if the task occurs on targetDate, false otherwise.
     */
    public boolean isOnDate(LocalDate targetDate) {
        return false;
    }

    @Override
    public String toTextLine() {
        return (isDone ? "1" : "0") + " | " + description;
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
