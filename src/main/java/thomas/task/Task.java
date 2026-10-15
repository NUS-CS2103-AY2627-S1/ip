package thomas.task;

import java.time.LocalDate;

/**
 * Represents a task in the task list.
 * A task consists of a description and a completion status.
 */
public class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Creates a task with the given description.
     * The task is initially marked as not done.
     *
     * @param description Textual description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the status icon representing task completion.
     *
     * @return "X" if the task is completed, otherwise a space.
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
     * Marks this task as incomplete.
     */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns whether this task is done.
     *
     * @return True if the task is completed.
     */
    public boolean isDone() {
        return this.isDone;
    }

    /**
     * Returns the task description.
     *
     * @return Description of the task.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns whether this task occurs on the given date.
     * Base tasks without a date or time return false.
     *
     * @param date Date to check against.
     * @return True if the task happens on the date; otherwise false.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns a formatted string for file storage.
     *
     * @return Formatted string representation for disk storage.
     */
    public String toFileFormat() {
        return (isDone ? "Y" : "N") + " | " + description;
    }

    /**
     * Returns the status and description of this task.
     *
     * @return Status icon and description as a string.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
