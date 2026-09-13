package xuan.task;

/**
 * Represents an event task with a start time and an end time.
 */
public class Event extends Task {
    private String from;
    private String to;

    /**
     * Creates an Event task with the given description, start time, and end time.
     *
     * @param description the description of the event
     * @param from the start time of the event
     * @param to the end time of the event
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the start time of the event.
     *
     * @return the start time of the event
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the end time of the event.
     *
     * @return the end time of the event
     */
    public String getTo() {
        return to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + from + " to: " + to + ")";
    }
}
