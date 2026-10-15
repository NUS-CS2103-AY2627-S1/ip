package dukey.task;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

/**
 * Represents a task in the chatbot's task list.
 */

public class Task {
    public static final DateTimeFormatter STORAGE_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    protected String description;
    protected boolean isDone;
    protected LocalDateTime doneAt;

    /**
     * Creates an undone task with the given description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        this(description, false);
    }

    /**
     * Creates a task with the given description and completion status.
     *
     * @param description Description of the task.
     * @param isDone Whether the task is already done.
     */
    public Task(String description, boolean isDone) {
        this.description = description;
        this.isDone = isDone;
        this.doneAt = null;
    }

    /**
     * Returns the display icon for the task completion status.
     *
     * @return X if done, or a blank space otherwise.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as done at the given date/time.
     *
     * @param doneAt Date/time this task was completed.
     */
    public void markAsDone(LocalDateTime doneAt) {
        this.isDone = true;
        this.doneAt = doneAt;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsUndone() {
        this.isDone = false;
        this.doneAt = null;
    }

    /**
     * Returns whether this task is done.
     *
     * @return True if this task is done.
     */
    public boolean isDone() {
        return this.isDone;
    }

    /**
     * Returns when this task was completed.
     *
     * @return Date/time this task was completed, or null if unknown.
     */
    public LocalDateTime getDoneAt() {
        return this.doneAt;
    }

    /**
     * Checks whether this task was completed in the calendar week containing the given date.
     *
     * @param date Date whose calendar week should be checked.
     * @return True if this task was completed in that calendar week.
     */
    public boolean wasCompletedInCalendarWeek(LocalDate date) {
        if (!this.isDone || this.doneAt == null) {
            return false;
        }

        LocalDate completedDate = this.doneAt.toLocalDate();
        LocalDate weekStart = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        return !completedDate.isBefore(weekStart) && !completedDate.isAfter(weekEnd);
    }

    /**
     * Checks whether this task was completed in the calendar month containing the given date.
     *
     * @param date Date whose calendar month should be checked.
     * @return True if this task was completed in that calendar month.
     */
    public boolean wasCompletedInCalendarMonth(LocalDate date) {
        if (!this.isDone || this.doneAt == null) {
            return false;
        }

        YearMonth completedMonth = YearMonth.from(this.doneAt);
        return completedMonth.equals(YearMonth.from(date));
    }

    /**
     * Checks whether this task occurs on the given date.
     *
     * @param date Date to check against.
     * @return False for a plain task.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    public boolean containsKeyword(String keyword) {
        return this.description.contains(keyword);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", this.getStatusIcon(), this.description);
    }

    /**
     * Returns a storage representation of this task.
     *
     * @return Empty string for an unknown base task type.
     */
    public String toFileString() {
        return "";
    }

    /**
     * Formats the completion timestamp for saving.
     *
     * @return Completion timestamp with a leading space, or an empty string if it is unknown.
     */
    protected String getDoneAtStorageText() {
        if (this.doneAt == null) {
            return "";
        }

        return " " + this.doneAt.format(STORAGE_DATE_TIME_FORMAT);
    }
}
