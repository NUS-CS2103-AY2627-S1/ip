package sandrone.task;

import java.time.LocalDate;

/**
 * Represents a task with a description and a completion status.
 */
public class Task {
    /** Text describing what the user needs to do. */
    protected String description;

    /** Whether the task has been completed. */
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

    /**
     * Returns the symbol used when displaying this task's completion status.
     *
     * @return {@code "X"} when the task is done; otherwise, a space
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Returns whether this task has been completed. */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns this task's description.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
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
     * Returns whether this task is associated with the given calendar date.
     * Tasks without a date, such as todos, are not associated with any date.
     *
     * @param date the date to check
     * @return {@code false} for a task with no date
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns this task in the format used for display.
     */
    @Override
    public String toString() {
        return "[" + this.getStatusIcon()
            + "] " + this.getDescription();
    }

    /**
     * Returns this task in the format used in the save file.
     */
    public String toFileFormat() {
        return (this.isDone ? "1" : "0") + " | " + description;
    }
}
