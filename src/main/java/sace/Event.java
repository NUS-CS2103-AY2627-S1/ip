package sace;

/**
 * Represents a task that occurs between specified start and end times.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates an incomplete event with the given description and time range.
     *
     * @param description description of the event.
     * @param from starting date or time of the event.
     * @param to ending date or time of the event.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the starting information of this event.
     *
     * @return event start.
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the ending information of this event.
     *
     * @return event end.
     */
    public String getTo() {
        return to;
    }

    /**
     * Returns this event with its type, completion status, and time range.
     *
     * @return displayable representation of this event.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
