package duncan.task;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that needs to be done before a specific date,
 * e.g. "return book by 2019-12-02". The date is stored as a
 * {@link LocalDate}, so it is validated on input and can be displayed
 * in a friendlier format than it was typed in.
 */
public class Deadline extends Task {
    /** Format the date is displayed in, e.g. "Dec 2 2019". */
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy");

    protected LocalDate by;

    /**
     * Creates a deadline that is not done yet.
     *
     * @param description What the task is, as typed by the user.
     * @param by The date this task is due.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        assert by != null;
        this.by = by;
    }

    /**
     * Moves this deadline to a new due date. Whether it is done is unchanged.
     *
     * @param newBy The new date this task is due.
     */
    public void reschedule(LocalDate newBy) {
        assert newBy != null;
        by = newBy;
    }

    /**
     * Pushes this deadline's due date back by the given number of days.
     * Whether it is done is unchanged.
     *
     * @param days How many days later the task is due, at least 1.
     * @throws DateTimeException If the new date is beyond the latest supported date,
     *     in which case the due date is left unchanged.
     */
    public void snooze(int days) throws DateTimeException {
        assert days >= 1;
        by = by.plusDays(days);
    }

    /** Returns whether {@code other} is the same task, as {@link Task} defines it, with the same due date. */
    @Override
    public boolean isDuplicateOf(Task other) {
        return super.isDuplicateOf(other) && other instanceof Deadline deadline && by.equals(deadline.by);
    }

    /** Returns this deadline as a file line, e.g. "D\t0\treturn book\t2019-12-02". */
    @Override
    public String toFileFormat() {
        return "D" + FIELD_SEPARATOR + getSharedFileFields() + FIELD_SEPARATOR + by;
    }

    /** Returns this deadline for display, e.g. "[D][ ] return book (by: Dec 2 2019)". */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }
}
