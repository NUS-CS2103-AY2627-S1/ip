package nob.task;

import java.time.LocalDateTime;

import nob.util.DateTimeUtil;

/**
 * Represents a task that takes place between a start and end time.
 */
public class Event extends Task {
    /** The parsed start date or time supplied by the user, when it can be parsed. */
    private final LocalDateTime fromDateTime;

    /** The parsed end date or time supplied by the user, when it can be parsed. */
    private final LocalDateTime toDateTime;

    /** Original event start text used for display fallback when the value is not a recognised date/time. */
    private final String fromText;

    /** Original event end text used for display fallback when the value is not a recognised date/time. */
    private final String toText;

    /**
     * Creates an incomplete event task.
     *
     * @param description Text describing the event.
     * @param from The event start date or time.
     * @param to The event end date or time.
     */
    public Event(String description, String from, String to) {
        super(description);
        assert from != null : "Event start times must not be null";
        assert !from.isBlank() : "Event start times must not be blank after parsing";
        assert to != null : "Event end times must not be null";
        assert !to.isBlank() : "Event end times must not be blank after parsing";
        this.fromDateTime = DateTimeUtil.parseDateTime(from).orElse(null);
        this.toDateTime = DateTimeUtil.parseDateTime(to).orElse(null);
        this.fromText = from == null ? "" : from.trim();
        this.toText = to == null ? "" : to.trim();
    }

    /**
     * Returns this event in the format used when displaying a task list.
     *
     * @return the event time frame, description, and completion status
     */
    @Override
    public String toString() {
        String fromValue = fromDateTime == null ? fromText : DateTimeUtil.formatDisplay(fromDateTime);
        String toValue = toDateTime == null ? toText : DateTimeUtil.formatDisplay(toDateTime);
        return "[E: " + fromValue + " to " + toValue + "] " + super.toString();
    }
}
