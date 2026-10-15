package task;

import michael.MichaelException;

/**
 * Represents a task that happens between a start and end time.
 */
public class Event extends Task {
    /** The event start time. */
    private final String from;

    /** The event end time. */
    private final String to;

    /**
     * Creates an incomplete event task.
     *
     * @param description the text describing the event
     * @param from the event start time
     * @param to the event end time
     * @throws MichaelException if the task description is empty
     */
    public Event(String description, String from, String to) throws MichaelException {
        super(description, "event");
        assert from != null && !from.isBlank() : "An event must have a start time";
        assert to != null && !to.isBlank() : "An event must have an end time";
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event start time.
     *
     * @return the event start time
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the event end time.
     *
     * @return the event end time
     */
    public String getTo() {
        return to;
    }

    /**
     * Returns the text used to display this event task.
     *
     * @return the event task type, status, description, and time range
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
