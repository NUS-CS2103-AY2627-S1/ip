package chris.task;

/**
 * Represents a task that occurs between specified start and end times.
 */
public class Event extends Task {
    /** Start information as entered by the user. */
    protected String from;

    /** End information as entered by the user. */
    protected String to;

    /**
     * Creates an incomplete event task.
     *
     * @param description Description of the event task.
     * @param from Start information as entered by the user.
     * @param to End information as entered by the user.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns whether another event has the same description, start, and end details.
     *
     * @param otherTask Task to compare with this event.
     * @return {@code true} if both events have matching details.
     */
    @Override
    boolean hasSameDetails(Task otherTask) {
        if (!super.hasSameDetails(otherTask)) {
            return false;
        }
        Event otherEvent = (Event) otherTask;
        return this.from.equalsIgnoreCase(otherEvent.from)
                && this.to.equalsIgnoreCase(otherEvent.to);
    }

    /**
     * Returns this event task in the format used for persistent storage.
     *
     * @return Serialized event task.
     */
    @Override
    public String toDataString() {
        return "E | " + getStatusNumber() + " | " + this.description
                + " | " + this.from + " | " + this.to;
    }

    /**
     * Returns this event task in its display format.
     *
     * @return The event type, status, description, and timing information.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + this.from + " to: " + this.to + ")";
    }
}
