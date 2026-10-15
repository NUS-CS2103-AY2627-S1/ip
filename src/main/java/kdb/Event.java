package kdb;

/** Represents a task that takes place during a stated period. */
public class Event extends Task {
    protected String from;
    protected String to;

    /**
     * Creates an event with a description, start time, and end time.
     *
     * @param description text describing the event
     * @param from event start time
     * @param to event end time
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /** Creates an event with a priority. */
    public Event(String description, String from, String to, Priority priority) {
        super(description, priority);
        this.from = from;
        this.to = to;
    }

    /** Returns a human-readable representation of this event. */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }

    /** Returns the representation used when saving this event. */
    @Override
    public String toFileFormat() {
        return "E | " + (isDone() ? "1" : "0") + " | " + getDescription()
                        + " | " + from + " | " + to;
    }
}
