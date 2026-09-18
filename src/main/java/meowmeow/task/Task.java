package meowmeow.task;

import java.time.LocalDate;

/**
 * Represents a task with a description and a completion status.
 * Serves as the base class for all specific task types.
 */
public class Task {
    /** Description of what the task involves */
    protected String description;
    /** Whether the task has been completed */
    protected boolean isDone;

    /**
     * Constructs a task with the given description, initially not done.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
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
     * Marks this task as not yet completed.
     */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns the icon representing this task's completion status.
     *
     * @return "X" if the task is done, a single space otherwise.
     */
    public String getStatusIcon() {
        // mark done task with X
        return (isDone ? "X" : " ");
    }

    /**
     * Returns this task encoded as a single line for file storage.
     *
     * @return Encoded string in the form "status | description".
     */
    public String toFileFormat() {
        return (isDone ? "1" : "0") + " | " + description;
    }

    /**
     * Returns whether this task falls on the given date.
     * Tasks with no date attached never match.
     *
     * @param date Date to check against.
     * @return True if this task occurs on that date.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns whether this task's description contains the given text.
     *
     * @param lowerKeyword Search text, already converted to lower case.
     * @return True if the description contains the text, ignoring case.
     */
    public boolean matches(String lowerKeyword) {
        return description.toLowerCase().contains(lowerKeyword);
    }

    /**
     * Returns whether this task represents the same work as the given task.
     * Tasks of different types are never duplicates. Completion status is
     * ignored, so re-adding a finished task is still reported as a duplicate.
     *
     * @param other Task to compare against.
     * @return True if both tasks describe the same work.
     */
    public boolean isDuplicateOf(Task other) {
        return other != null
                && getClass() == other.getClass()
                && description.equalsIgnoreCase(other.description);
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
