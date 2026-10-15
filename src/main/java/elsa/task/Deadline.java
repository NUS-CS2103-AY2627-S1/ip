package elsa.task;

import java.time.LocalDate;

import elsa.Dates;

/**
 * A task that has to be done before a stated date,
 * for example "return book (by: Oct 15 2019)".
 */
public class Deadline extends Task {
    /**
     * The date the task is due. A LocalDate rather than text, so that the date
     * is known to be a real one and could later be compared with another.
     */
    protected LocalDate by;

    /**
     * Creates a deadline that is not done yet.
     *
     * @param description the text describing what is to be done
     * @param by          the date it has to be done by
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the date the task is due.
     *
     * @return the due date
     */
    public LocalDate getBy() {
        return by;
    }

    /**
     * Returns whether this deadline falls on the given date, which it does on
     * the one day it is due.
     *
     * @param date the date being asked about
     * @return true if the deadline is due on that date
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return by.equals(date);
    }

    @Override
    public boolean isSameTask(Task other) {
        // The cast is safe: the inherited check has already confirmed the kinds match.
        return super.isSameTask(other) && by.equals(((Deadline) other).by);
    }

    /**
     * Returns whether this deadline has passed without being done.
     * A deadline that is already done is not overdue however old it is, which is
     * why the check asks the task whether it is done as well as when it was due.
     *
     * @return true if the due date is in the past and the task is not done
     */
    public boolean isOverdue() {
        return !isDone && by.isBefore(Dates.today());
    }

    /**
     * Returns the deadline as it should appear to the user,
     * for example "[D][ ] return book (by: Oct 15 2019)".
     *
     * @return the deadline marker, the inherited task text, and the due time
     */
    @Override
    public String toString() {
        // The note is added after the date rather than inside the brackets, so
        // that the date itself still reads as one piece.
        String overdue = isOverdue() ? " -- overdue" : "";
        return "[D]" + super.toString() + " (by: " + Dates.format(by) + ")" + overdue;
    }

    /**
     * Returns the deadline as one line of the data file,
     * for example "D | 0 | return book | 2019-10-15".
     *
     * @return the deadline's type letter, the inherited fields, and the due time
     */
    @Override
    public String toSaveFormat() {
        return TaskFormat.DEADLINE + TaskFormat.SEPARATOR + super.toSaveFormat()
                + TaskFormat.SEPARATOR + Dates.toSaveFormat(by);
    }
}
