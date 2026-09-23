package sallman.task;

import java.time.LocalDate;
import java.util.Optional;

/**
 * A task that must be done before a given point in time,
 * e.g. {@code submit report} by {@code 2019-10-15}.
 */
public class Deadline extends Task {

    /** When the task is due. */
    protected LocalDate by;

    /**
     * Creates a deadline that is initially not done.
     *
     * @param description what the task involves
     * @param by          when the task is due
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns whether this deadline falls due on the given date.
     *
     * @param date the date being asked about
     * @return true if the task is due that day
     */
    @Override
    public boolean isOn(LocalDate date) {
        return by.equals(date);
    }

    /**
     * Returns the due date, which is when this deadline comes up.
     *
     * @return the date the task is due
     */
    @Override
    public Optional<LocalDate> getSortDate() {
        return Optional.of(by);
    }

    /**
     * Returns the due date as a saved field.
     *
     * @return the separator followed by the date in the input format
     */
    @Override
    protected String dateFields() {
        // Saved in the input format, so the file stays readable and
        // reloadable regardless of how dates are displayed.
        return SEPARATOR + by;
    }

    /**
     * Returns the due date as it is shown, e.g. {@code " (by: Oct 15 2019)"}.
     *
     * @return the due date in brackets, after the description
     */
    @Override
    protected String details() {
        return " (by: " + TaskDate.format(by) + ")";
    }

    /**
     * Returns an independent copy of this deadline.
     *
     * @return a deadline equal to this one, sharing none of its mutable state
     */
    @Override
    public Task copy() {
        return copyInto(new Deadline(description, by));
    }

    /**
     * Returns this deadline as one line of the data file,
     * e.g. {@code D | 0 | return book | 2019-10-15}.
     */
    @Override
    public String toFileFormat() {
        return "D" + SEPARATOR + super.toFileFormat();
    }

    /**
     * Returns this deadline as it should appear to the user,
     * e.g. {@code [D][ ] return book (by: Oct 15 2019)}.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString();
    }
}
