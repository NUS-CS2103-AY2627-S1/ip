package chatty.task;

/**
 * Represents a task that occurs between specified start and end values.
 */
public class Event extends Task {
    /** Date or time at which this event starts. */
    private final String from;

    /** Date or time at which this event ends. */
    private final String to;

    /**
     * Creates an incomplete event with the given description and time range.
     *
     * @param description description of the event.
     * @param from date or time at which the event starts.
     * @param to date or time at which the event ends.
     */
    public Event(String description, String from, String to) {
        super(description);
        assert from != null && to != null : "Event start and end values must not be null";
        this.from = from;
        this.to = to;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTypeIcon() {
        return "E";
    }

    /**
     * Returns the date or time at which this event starts.
     *
     * @return event start value.
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the date or time at which this event ends.
     *
     * @return event end value.
     */
    public String getTo() {
        return to;
    }

    /**
     * Returns this event with its start and end values.
     *
     * @return formatted event information.
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
