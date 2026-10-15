package ff15.task;

/**
 * Represents a task that needs to be done before a specific date, optionally at
 * a specific time of day. The moment is held as a {@link TaskTime} rather than
 * plain text, so that it is validated when it is typed and can be compared
 * against a {@link DateRange}.
 */
public class Deadline extends Task {
    /** When the task is due. */
    protected TaskTime date;

    /**
     * Creates a deadline.
     *
     * @param description what the user typed to describe the task.
     * @param by the date, and optionally the time, the task is due.
     */
    public Deadline(String description, TaskTime by) {
        super(description);
        this.date = by;
    }

    /** Two deadlines are the same when they match as tasks and are due at the same time. */
    @Override
    public boolean isSameAs(Task other) {
        return super.isSameAs(other) && date.equals(((Deadline) other).date);
    }

    /** A deadline falls in a span when the day it is due does. */
    @Override
    public boolean occursIn(DateRange range) {
        return range.includes(date.getDate());
    }

    /**
     * Returns this task with its due date, e.g.
     * {@code [D][ ] return book (by: Dec 02 2019)}.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + date + ")";
    }

    /**
     * Returns this task in save-file form, e.g.
     * {@code D | 0 | return book | 2019-12-02}.
     */
    @Override
    public String toFileFormat() {
        return "D | " + super.toFileFormat() + " | " + date.toFileFormat();
    }
}
