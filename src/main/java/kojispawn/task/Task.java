package kojispawn.task;

import java.time.LocalDate;

/**
 * Represents a task with a description and completion status.
 */
public abstract class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the icon representing the task's completion status.
     *
     * @return {@code X} when complete, or a space when incomplete.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Marks this task as complete.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns an independently mutable copy with the same type, data, and completion status.
     * Undo snapshots must not share mutable task state with the live list.
     *
     * @return Independent copy of this task.
     */
    abstract Task copy();

    protected void copyCompletionStatusTo(Task copy) {
        if (isDone) {
            copy.markAsDone();
        }
    }

    /**
     * Converts this task into the stable format used in the data file.
     *
     * @return Serialized task data.
     */
    public abstract String toDataString();

    /**
     * Reports whether this task occurs on a given date.
     * Tasks without a structured date do not occur on any queryable date.
     *
     * @param date Date to check.
     * @return {@code true} if the task occurs on the date.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Reports whether the task description contains the given keyword.
     *
     * @param keyword Keyword to find.
     * @return {@code true} if the description contains the keyword.
     */
    public boolean matches(String keyword) {
        return description.contains(keyword);
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
