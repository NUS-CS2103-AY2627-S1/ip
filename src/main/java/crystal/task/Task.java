package crystal.task;

import java.time.LocalDate;
import java.util.Locale;

/**
 * Represents a task with a description and completion status.
 */
public class Task {
    /** User-visible description of this task. */
    protected String description;
    /** Whether this task has been completed. */
    protected boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the symbol used to display the task's completion status.
     *
     * @return {@code X} if the task is done, or a space otherwise.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return {@code true} if this task is done.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the common fields used to store this task on disk.
     *
     * @return pipe-separated completion status and description.
     */
    public String toDataString() {
        return (isDone ? "1" : "0") + " | " + description;
    }

    /**
     * Returns whether this task occurs on a calendar date.
     * Tasks without calendar dates do not occur on any specific date.
     *
     * @param date date to check.
     * @return {@code true} if this task occurs on the date.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns whether this task's description contains a keyword, ignoring case.
     *
     * @param keyword keyword to search for.
     * @return {@code true} if the description contains the keyword.
     */
    public boolean hasDescriptionContaining(String keyword) {
        String normalizedDescription = description.toLowerCase(Locale.ROOT);
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return normalizedDescription.contains(normalizedKeyword);
    }

    /**
     * Returns the task's status icon followed by its description.
     *
     * @return formatted task details.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
