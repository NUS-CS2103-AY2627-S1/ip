package verity.task;

import java.time.LocalDate;

/**
 * Represents a task that starts and ends on specific dates.
 */
public class Event extends Task {

    /** Date this event starts. */
    protected LocalDate from;
    /** Date this event ends. */
    protected LocalDate to;

    /**
     * Creates an event with the given description, start, and end date.
     *
     * @param description Description of the event.
     * @param from Date the event starts.
     * @param to Date the event ends.
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the date this event starts.
     *
     * @return Start date of this event.
     */
    public LocalDate getFrom() {
        return from;
    }

    /**
     * Returns the date this event ends.
     *
     * @return End date of this event.
     */
    public LocalDate getTo() {
        return to;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from.format(DATE_DISPLAY_FORMAT)
                + " to: " + to.format(DATE_DISPLAY_FORMAT) + ")";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toSaveFormat() {
        return "E | " + super.toSaveFormat() + " | " + from + " | " + to;
    }
}
