package larry;

/**
 * Represents an event that occurs between specified start and end times.
 */
public class Event extends Task {
    private String from;
    private String to;

    /**
     * Creates an event with the specified description, start time, and end time.
     *
     * @param description Description of the event.
     * @param from Start time of the event.
     * @param to End time of the event.
     */
    public Event(String description, String from, String to) {
        super(description, TaskType.EVENT);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the start time of this event.
     *
     * @return Start time of the event.
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the end time of this event.
     *
     * @return End time of the event.
     */
    public String getTo() {
        return to;
    }

    /**
     * Returns a display-friendly representation of this event.
     *
     * @return Event type, status, description, start time, and end time.
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
