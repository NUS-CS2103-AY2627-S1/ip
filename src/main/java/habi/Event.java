package habi;

/**
 * Represents a task that takes place between two specified times.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates an incomplete event.
     *
     * @param description Text describing the event.
     * @param from Start time entered by the user.
     * @param to End time entered by the user.
     */
    public Event(String description, String from, String to) {
        super(TaskType.EVENT, description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event's start value.
     *
     * @return Event start.
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the event's end value.
     *
     * @return Event end.
     */
    public String getTo() {
        return to;
    }

    /** Returns the formatted start and end details shown after the description. */
    @Override
    protected String getTimingDetails() {
        return " (from: " + from + " to: " + to + ")";
    }

    /** Returns this event in the persistent storage format. */
    @Override
    public String toDataString() {
        return super.toDataString() + "\t" + from + "\t" + to;
    }
}
