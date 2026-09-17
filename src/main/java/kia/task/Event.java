package kia.task;

/**
 * A task with a start date or time and an end date or time.
 */
public class Event extends Task {
    /** Start date or time of the event. */
    protected String from;
    /** End date or time of the event. */
    protected String to;

    /**
     * Creates an incomplete event task.
     *
     * @param description the event description
     * @param from the event start date or time
     * @param to the event end date or time
     */
    public Event(String description, String from, String to) {
        super(description, TaskType.EVENT);
        assert from != null && !from.isBlank() : "An event must have a start value.";
        assert to != null && !to.isBlank() : "An event must have an end value.";
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event start value.
     *
     * @return the event start value
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the event end value.
     *
     * @return the event end value
     */
    public String getTo() {
        return to;
    }

    /**
     * Formats this event with its type, completion state, description, and time range.
     *
     * @return the user-facing event representation
     */
    @Override
    public String toString() {
        return "[" + getTypeIcon() + "]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }

    /**
     * Serializes this event with its start and end values for persistence.
     *
     * @return a pipe-delimited event record
     */
    @Override
    public String toStorageString() {
        return super.toStorageString() + " | " + from + " | " + to;
    }
}
