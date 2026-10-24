package echo.task;

import java.time.LocalDate;

/**
 * A task that must be completed by a given date.
 */
public class Deadline extends Task {
    protected LocalDate by;

    /**
     * Creates a Deadline task due on the given date.
     *
     * @param description What needs to be done.
     * @param by The date it is due.
     */
    public Deadline(String description, LocalDate by) {
        super(description, TaskType.DEADLINE);
        this.by = by;
    }

    /**
     * Returns this task's display line plus its due date, e.g.
     * "[D][ ] return book (by: Jun 06 2019)".
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + by.format(DISPLAY_DATE_FORMAT) + ")";
    }

    /**
     * Returns this task's save-file line plus its due date in ISO
     * format, e.g. "D | 0 | return book | 2019-06-06".
     */
    @Override
    public String toFileFormat() {
        return super.toFileFormat() + FILE_FORMAT_SEPARATOR + by;
    }

    /**
     * Returns true if this deadline's due date equals the given date.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return by.equals(date);
    }

    /**
     * Returns whether the given task is a duplicate of this one: same
     * description (per the superclass check) and the same due date.
     */
    @Override
    public boolean isDuplicateOf(Task other) {
        return super.isDuplicateOf(other) && this.by.equals(((Deadline) other).by);
    }
}
