package larp;

/**
 * Represents a task that occurs between specified start and end times.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates an incomplete event with the specified description and times.
     *
     * @param description Description of the event.
     * @param from Start time of the event.
     * @param to End time of the event.
     */
    public Event(String description, String from, String to) {
        this(description, from, to, false);
    }

    /**
     * Creates an event with its saved completion state.
     *
     * @param description Description of the event.
     * @param from Start time of the event.
     * @param to End time of the event.
     * @param isDone Whether the event has been completed.
     */
    public Event(String description, String from, String to, boolean isDone) {
        super(description, isDone);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event's start time.
     *
     * @return Start time.
     */
    public String getFrom() {
        return this.from;
    }

    /**
     * Returns the event's end time.
     *
     * @return End time.
     */
    public String getTo() {
        return this.to;
    }

    /**
     * Returns a display-friendly representation of this event.
     *
     * @return Formatted event description, status, and times.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + this.from + " to: " + this.to + ")";
    }
}
