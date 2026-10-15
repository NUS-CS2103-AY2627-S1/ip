package kafka.task;

/**
 * Represents an event with specified start and end values.
 * Both values are stored as display-ready text.
 */
public class Event extends Task {
    private String from;
    private String to;

    /**
     * Creates an unfinished event.
     *
     * @param description Activity taking place.
     * @param from Display-ready start date or time.
     * @param to Display-ready end date or time.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Copies an event for an edit that has not yet been saved.
     */
    private Event(Event source) {
        super(source);
        this.from = source.from;
        this.to = source.to;
    }

    @Override
    Task copy() {
        return new Event(this);
    }

    /**
     * Adds the event marker and time range to the common task display.
     *
     * @return Display text beginning with {@code [E]}.
     */
    @Override
    public String display() {
        return "[E]" + super.display() + " (from: " + from + " to: " + to + ")";
    }

    /**
     * Serializes this event for the task data file.
     *
     * @return Event data including its start and end values.
     */
    @Override
    public String toDataString() {
        return super.toDataString("E") + " | " + from + " | " + to;
    }

    void rescheduleFrom(String newFrom) {
        from = newFrom;
    }

    void rescheduleTo(String newTo) {
        to = newTo;
    }
}
