package suki;

import java.util.Optional;

/**
 * Represents a task that needs to be done before a specific date/time.
 */
public class Deadline extends Task {
    /** When the task is due. */
    protected DateTime by;

    /**
     * Creates a deadline that is not yet done.
     *
     * @param description what the user wants to do
     * @param by when it is due
     */
    public Deadline(String description, DateTime by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns when this task is due.
     *
     * @return the due date/time
     */
    public DateTime getBy() {
        return by;
    }

    /**
     * {@inheritDoc}
     *
     * @return the due date/time
     */
    @Override
    public Optional<DateTime> getScheduledDateTime() {
        return Optional.of(by);
    }

    /**
     * {@inheritDoc}
     *
     * @return "D", marking this as a deadline
     */
    @Override
    public String getTypeIcon() {
        return "D";
    }

    /**
     * Returns the task as shown to the user, with its due date appended,
     * e.g. {@code [D][ ] return book (by: Oct 15 2019)}.
     *
     * @return the display text for this deadline
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + by + ")";
    }
}
