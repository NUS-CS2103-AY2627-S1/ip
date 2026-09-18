package fein.task;

/** A task with a specified start and end date or time. */
public class Event extends Task {
    /** The user-provided event start value, kept as text. */
    protected String from;

    /** The user-provided event end value, kept as text. */
    protected String to;

    /** Creates an incomplete event task. */
    public Event(String description, String from, String to) {
        super(description);
        // Parser and storage require both endpoints so every event has a complete range.
        assert from != null && !from.isBlank() : "An event must have non-blank start text";
        assert to != null && !to.isBlank() : "An event must have non-blank end text";
        this.from = from;
        this.to = to;
    }

    /** Returns the user-provided event start text. */
    public String getFrom() {
        return from;
    }

    /** Returns the user-provided event end text. */
    public String getTo() {
        return to;
    }

    /** Returns whether another event has the same description and time range as this event. */
    @Override
    public boolean hasSameDetailsAs(Task other) {
        if (!(other instanceof Event event)) {
            return false;
        }
        return super.hasSameDetailsAs(event)
                && from.equalsIgnoreCase(event.from)
                && to.equalsIgnoreCase(event.to);
    }

    /** Returns this event with its type and time-range markers. */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
