package tally.task;

import java.time.LocalDate;

/** A task that has to be finished before a stated date. */
public class Deadline extends Task {
    /** The letter standing for this kind of task in the data file. */
    public static final String TYPE = "D";

    private final LocalDate dueDate;

    /**
     * Creates a deadline that is not done yet.
     *
     * @param description what has to be done.
     * @param dueDate the date it has to be done by.
     */
    public Deadline(String description, LocalDate dueDate) {
        super(description);
        this.dueDate = dueDate;
    }

    /**
     * Returns whether this deadline takes up a given day, which only its due date is.
     *
     * @param day the day being considered.
     * @return true when the day is the day it is due.
     */
    @Override
    public boolean occupies(LocalDate day) {
        return day.equals(dueDate);
    }

    /**
     * Returns this deadline as the user sees it, tagged with its type and due date.
     *
     * @return for example "[D][ ] return book (by: Oct 15 2019)".
     */
    @Override
    public String toString() {
        return String.format("[%s]%s (by: %s)", TYPE, super.toString(), formatDate(dueDate));
    }

    /**
     * Returns the line standing for this deadline in the data file.
     *
     * <p>The date is written the way LocalDate prints itself, which is the same
     * yyyy-mm-dd form that LocalDate.parse reads, so it survives a round trip.
     *
     * @return for example "D | 0 | return book | 2019-10-15".
     */
    @Override
    public String toSaveFormat() {
        return TYPE + FIELD_SEPARATOR + toSharedSaveFields() + FIELD_SEPARATOR + dueDate;
    }
}
