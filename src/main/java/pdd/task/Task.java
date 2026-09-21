package pdd.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * A single to-do item in the task list. Concrete subclasses ({@link Todo},
 * {@link Deadline}, {@link Event}) add whatever date/time information their
 * kind of task needs; this class owns the description and done/not-done
 * status shared by all of them.
 */
public abstract class Task {
    /** Shared display format for task dates, e.g. "Oct 15 2019". Public: used by pdd.ui.Ui as well. */
    public static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");

    /**
     * Field separator used in the save-file format; shared so the write side
     * ({@code toFileFormat()} implementations) and the read side ({@code
     * pdd.storage.Storage}) can't drift out of sync.
     */
    public static final String FIELD_SEPARATOR = " | ";

    protected String description;
    protected boolean isDone;

    /** Creates a not-done task with the given description. */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /** Returns the single-character status icon shown in the task list: {@code X} if done, a space otherwise. */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /** Returns 1 if this task is done, 0 otherwise, for use in the save-file format. */
    protected int getStatusValue() {
        return isDone ? 1 : 0;
    }

    /** Returns this task's description. */
    public String getDescription() {
        return description;
    }

    /** Marks this task as done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void markAsNotDone() {
        isDone = false;
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }

    /** Serializes this task to a single line in the save-file format. */
    public abstract String toFileFormat();

    /** Returns whether this task occurs on the given date. Tasks without a date never match. */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns the date this task should be ordered by when sorting, or empty if
     * this task has no relevant date (e.g. a {@link Todo}). Used by {@code
     * TaskList#sortTasks()} to sort dated tasks chronologically.
     */
    public Optional<LocalDate> getSortDate() {
        return Optional.empty();
    }
}
