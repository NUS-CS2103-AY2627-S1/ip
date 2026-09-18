package vega;

/** Represents a task scheduled from one time to another. */
public class Event extends Task {
    private final String from;

    private final String to;

    /**
     * Creates an event with a description, start time, and end time.
     *
     * @param description Description of the event.
     * @param from Event start time.
     * @param to Event end time.
     */
    public Event(String description, String from, String to) {
        super(description, "E");
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event start time text for persistent storage.
     *
     * @return Event start time.
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the event end time text for persistent storage.
     *
     * @return Event end time.
     */
    public String getTo() {
        return to;
    }

    @Override
    public String toString() {
        return super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
