package nova.task;

/**
 * Represents a task that takes place between specified start and end times.
 */
public class Event extends Task {
    /**
     * Start date or time displayed for this event.
     */
    protected String from;

    /**
     * End date or time displayed for this event.
     */
    protected String to;

    /**
     * Creates an incomplete event.
     *
     * @param description description of the event.
     * @param from event start date or time.
     * @param to event end date or time.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns this event in the data-file format.
     *
     * @return serialized event
     */
    @Override
    public String toDataString() {
        return "V2 | E | " + getDataStatus() + " | " + encodeDataField(description)
                + " | " + encodeDataField(from) + " | " + encodeDataField(to);
    }

    /**
     * Returns this event in its display format.
     *
     * @return formatted event
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
