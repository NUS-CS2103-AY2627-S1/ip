package jarvis;

/** Represents a task with a stated start and end date or time. */
public class Event extends Task {
    /** Start date or time of the event. */
    private final String from;
    /** End date or time of the event. */
    private final String to;

    /** Creates an incomplete event task. */
    public Event(String description, String from, String to) {
        super(description);
        assert from != null : "Event start time must not be null";
        assert to != null : "Event end time must not be null";
        this.from = from;
        this.to = to;
    }

    /** Returns the event start date or time. */
    public String getFrom() {
        return from;
    }

    /** Returns the event end date or time. */
    public String getTo() {
        return to;
    }

    /** Returns the type marker used when displaying an event. */
    @Override
    protected String getTypeIcon() {
        return "E";
    }

    /** Returns the task followed by its start and end times. */
    @Override
    public String toString() {
        return super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
