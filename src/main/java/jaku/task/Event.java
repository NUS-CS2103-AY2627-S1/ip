package jaku.task;

/**
 * Represents a task that occurs between a given start and end date or time.
 */
public class Event extends Task {
    /** Date or time at which the event starts. */
    private final String from;

    /** Date or time at which the event ends. */
    private final String to;

    /**
     * Creates an incomplete event with the given description and timing text.
     *
     * @param description the text describing the event
     * @param from the date or time at which the event starts
     * @param to the date or time at which the event ends
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event's start text without display formatting.
     *
     * @return the start date or time text
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the event's end text without display formatting.
     *
     * @return the end date or time text
     */
    public String getTo() {
        return to;
    }

    /**
     * Returns this event with its task-type icon and timing text.
     *
     * @return the event formatted for display by Jaku
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
