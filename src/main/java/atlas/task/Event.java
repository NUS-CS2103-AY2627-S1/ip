package atlas.task;

/** Represents a task that takes place between two user-specified times. */
public class Event extends Task {
    /** Information describing when the event starts. */
    private final String from;

    /** Information describing when the event ends. */
    private final String to;

    /**
     * Creates an event with the given description and time information.
     *
     * @param description text describing the event.
     * @param from information about when the event starts.
     * @param to information about when the event ends.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event start information.
     *
     * @return information about when the event starts.
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the event end information.
     *
     * @return information about when the event ends.
     */
    public String getTo() {
        return to;
    }

    /**
     * Returns this event in event display format.
     *
     * @return the event type marker, task details, and time information.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
