package suki;

import java.util.Optional;

/**
 * Represents a task that starts and ends at specific dates/times.
 */
public class Event extends Task {
    /** When the event starts. */
    protected DateTime from;

    /** When the event ends. */
    protected DateTime to;

    /**
     * Creates an event that is not yet done.
     *
     * @param description what the event is
     * @param from when it starts
     * @param to when it ends
     */
    public Event(String description, DateTime from, DateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns when this event starts.
     *
     * @return the start date/time
     */
    public DateTime getFrom() {
        return from;
    }

    /**
     * Returns when this event ends.
     *
     * @return the end date/time
     */
    public DateTime getTo() {
        return to;
    }

    /**
     * {@inheritDoc}
     *
     * <p>An event is ordered by when it starts, not when it ends, because
     * that is when the user has to act on it.
     *
     * @return the start date/time
     */
    @Override
    public Optional<DateTime> getScheduledDateTime() {
        return Optional.of(from);
    }

    /**
     * {@inheritDoc}
     *
     * @return "E", marking this as an event
     */
    @Override
    public String getTypeIcon() {
        return "E";
    }

    /**
     * Returns the task as shown to the user, with its start and end appended,
     * e.g. {@code [E][ ] meeting (from: Oct 15 2019, 2:00PM to: Oct 15 2019, 4:00PM)}.
     *
     * @return the display text for this event
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
