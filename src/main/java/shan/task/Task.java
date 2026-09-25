package shan.task;

import java.time.LocalDate;
import java.util.Locale;

/**
 * Represents a task that can be marked as completed.
 */
public abstract class Task {
    private final String taskName;
    private boolean isDone = false;

    /**
     * Constructs a task.
     *
     * @param taskName Name of the task.
     */
    public Task(String taskName) {
        assert taskName != null : "Task name must not be null";
        assert !taskName.isBlank() : "Task name must not be blank";
        this.taskName = taskName;
    }

    /**
     * Marks this task as done.
     */
    public void markDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void unmarkDone() {
        this.isDone = false;
    }

    /**
     * Returns whether this task is completed.
     *
     * @return {@code true} when completed.
     */
    public boolean isDone() {
        return this.isDone;
    }

    /**
     * Returns whether this task occurs on the specified date.
     *
     * @param date Date to check.
     * @return {@code true} when this task occurs on the date.
     */
    public boolean occursOn(LocalDate date) {
        return occursBetween(date, date);
    }

    /**
     * Returns whether this task occurs within an inclusive date range.
     *
     * @param startDate First date in the range.
     * @param endDate   Last date in the range.
     * @return {@code true} when this task occurs within the range.
     */
    public boolean occursBetween(LocalDate startDate, LocalDate endDate) {
        return false;
    }

    /**
     * Returns whether this task's description contains the supplied keyword.
     * Matching is case-insensitive.
     *
     * @param keyword Keyword to find in the task description.
     * @return {@code true} when the description contains the keyword.
     */
    public boolean containsKeyword(String keyword) {
        return this.taskName.toLowerCase(Locale.ROOT)
                .contains(keyword.toLowerCase(Locale.ROOT));
    }

    /**
     * Returns the fields shared by all task types in the save-file format.
     *
     * @return Completion status and task name separated by {@code |}.
     */
    protected String getFileFields() {
        return String.format("%s | %s", this.isDone ? "1" : "0", this.taskName);
    }

    /**
     * Returns this task in the save-file format.
     *
     * @return Serialized task.
     */
    public abstract String toFileString();

    /**
     * Returns the display representation of this task.
     *
     * @return Display representation of this task.
     */
    @Override
    public String toString() {
        return String.format("[%s] %s", this.isDone ? "X" : " ", this.taskName);
    }
}
