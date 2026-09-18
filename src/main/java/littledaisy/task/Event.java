package littledaisy.task;

/**
 * A task that runs between two stated points in time, e.g.
 * {@code project meeting (from: Mon 2pm to: 4pm)}.
 */
public class Event extends Task {
    /** When the event starts, as the user typed it. */
    private final String from;

    /** When the event ends, as the user typed it. */
    private final String to;

    /**
     * Creates an event that is not done yet.
     *
     * @param description what the event is about
     * @param from when it starts, as the user typed it
     * @param to when it ends, as the user typed it
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the user-entered event start.
     *
     * @return event start text
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the user-entered event end.
     *
     * @return event end text
     */
    public String getTo() {
        return to;
    }

    /** Returns whether another event has the same description and time range. */
    @Override
    public boolean hasSameDetails(Task other) {
        if (!super.hasSameDetails(other)) {
            return false;
        }
        Event event = (Event) other;
        return from.equalsIgnoreCase(event.from) && to.equalsIgnoreCase(event.to);
    }

    /**
     * Returns this event with its type icon and time span, e.g.
     * {@code [E][ ] project meeting (from: Mon 2pm to: 4pm)}.
     *
     * @return the type icon, the inherited rendering, then the time span
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
