package mochi.task;

/**
 * Represents a task occurring between specified start and end times.
 */
public class Event extends Task {
    protected String from;
    protected String to;

    /**
     * Creates an incomplete event.
     *
     * @param description description of the event
     * @param from start date or time as entered by the user
     * @param to end date or time as entered by the user
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    private Event(Event source) {
        super(source);
        this.from = source.from;
        this.to = source.to;
    }

    /** {@inheritDoc} */
    @Override
    public Event copy() {
        return new Event(this);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }

    /** {@inheritDoc} */
    @Override
    public String toFileString() {
        return "E" + STORAGE_SEPARATOR + super.toFileString()
                + STORAGE_SEPARATOR + from + STORAGE_SEPARATOR + to;
    }
}
