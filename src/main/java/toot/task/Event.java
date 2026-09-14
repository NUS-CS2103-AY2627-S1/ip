package toot.task;

/**
 * Represents a task that occurs between specified start and end times.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates a not-done event with the given description and time text.
     *
     * @param description Description of the event.
     * @param from Date or time text at which the event starts.
     * @param to Date or time text at which the event ends.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event's start text.
     *
     * @return Start date or time text.
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the event's end text.
     *
     * @return End date or time text.
     */
    public String getTo() {
        return to;
    }

    /**
     * Returns the event with its type, status, and time range.
     *
     * @return Formatted event.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
