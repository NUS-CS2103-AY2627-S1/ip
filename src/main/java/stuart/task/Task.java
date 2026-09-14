package stuart.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Represents a single task tracked by Stuart: a description, a done/not-done
 * status, and a {@link TaskType}. Subclasses ({@link ToDos}, {@link Deadlines},
 * {@link Events}) add whatever date fields their kind of task needs, and
 * override the methods whose behaviour depends on those dates.
 */
public class Task {
    /** Formatter used to display deadline/event dates, e.g. "Oct 15 2019". */
    public static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");

    /** Field separator used in the save file, e.g. {@code "T | 1 | read book"}. */
    public static final String SAVE_DELIMITER = " | ";

    /** What the task is. */
    protected String description;
    /** Whether the task has been marked done. */
    protected boolean isDone;
    /** The kind of task this is. */
    protected TaskType type;

    /**
     * Creates a new, not-done task.
     *
     * @param description what the task is
     * @param type the kind of task this is
     */
    public Task(String description, TaskType type) {
        assert description != null : "task description should never be null";
        assert !description.isEmpty() : "Stuart rejects an empty description before ever constructing a task";
        this.description = description;
        this.isDone = false;
        this.type = type;
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns "X" if this task is done, or " " otherwise.
     *
     * @return the status icon
     */
    public String getStatusIcon() {
        return this.isDone ? "X" : " ";
    }

    /**
     * Returns this task formatted as "[T][X] description" or "[T][ ] description",
     * with the leading tag depending on {@link #type}.
     *
     * @return the formatted task line
     */
    @Override
    public String toString() {
        return "[" + type.getTag() + "][" + this.getStatusIcon() + "] " + this.description;
    }

    /**
     * Returns this task formatted for the save file, e.g. {@code "T | 1 | read book"}.
     *
     * @return the save-file line for this task, without a trailing newline
     */
    public String toSaveFormat() {
        return type.getTag() + SAVE_DELIMITER + (isDone ? "1" : "0") + SAVE_DELIMITER + description;
    }

    /**
     * Checks whether this task occurs on the given date. A plain to-do never
     * occurs on any date, since it has none.
     *
     * @param date the date to check
     * @return true if this task occurs on {@code date}
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns the date this task should be sorted by, if it has one.
     * A plain to-do has no date, so it sorts after every dated task.
     *
     * @return the sort date, or {@link Optional#empty()} if this task has none
     */
    public Optional<LocalDate> getSortDate() {
        return Optional.empty();
    }

    /**
     * Checks whether this task is overdue: its date has passed and it is
     * not yet done. A plain to-do is never overdue, since it has no date,
     * and a done task is never overdue regardless of its date.
     *
     * @return true if this task is overdue
     */
    public boolean isOverdue() {
        return false;
    }

    /**
     * Checks whether this task's relevant date falls within the next
     * {@code days} days (inclusive of today), and it is not yet done. A
     * plain to-do is never due soon, since it has no date.
     *
     * @param days how many days ahead to look
     * @return true if this task is due within {@code days} days
     */
    public boolean isDueSoon(int days) {
        return false;
    }

    /**
     * Checks whether this task has the same details as {@code other}: the
     * same concrete task type and description, and (for {@link Deadlines}/
     * {@link Events}) the same relevant date(s). Used to reject adding a
     * task that's an exact duplicate of one already in the list. Does not
     * consider done/not-done status, since that's state, not identity.
     *
     * @param other the task to compare against
     * @return true if {@code other} describes the same task as this one
     */
    public boolean hasSameDetails(Task other) {
        return getClass() == other.getClass() && description.equals(other.description);
    }

    /**
     * Checks whether this task contains the keyword inputted.
     * Works for all instances of Task as only requires the task description
     *
     * @param keyword the string to filter by
     * @return true if this task contains the keyword
     */
    public boolean containsKeyword(String keyword) {
        return this.description.contains(keyword);
    }
}
