package verity.task;

import java.time.LocalDate;

/**
 * Represents a task that needs to be done before a specific date.
 */
public class Deadline extends Task {

    /** Date by which this task should be done. */
    protected LocalDate by;

    /**
     * Creates a deadline with the given description and due date.
     *
     * @param description Description of the deadline.
     * @param by Date by which the task should be done.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the date by which this task should be done.
     *
     * @return Due date of this deadline.
     */
    public LocalDate getBy() {
        return by;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DATE_DISPLAY_FORMAT) + ")";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toSaveFormat() {
        return "D | " + super.toSaveFormat() + " | " + by;
    }
}
