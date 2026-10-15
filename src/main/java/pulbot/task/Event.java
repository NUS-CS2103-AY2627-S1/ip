package pulbot.task;

import java.time.LocalDateTime;

import pulbot.Pulbot;

/** Represents a task that takes place during a date and time range. */
public class Event extends Task {
    private static final String INVALID_DATE_RANGE_MESSAGE =
            "The event end time must be after its start time.";
    private final LocalDateTime from;
    private final LocalDateTime to;

    public Event(String description, String from, String to) {
        this(description, Pulbot.parseDateTime(from), Pulbot.parseDateTime(to));
    }

    /** Creates an event with already parsed start and end date-times. */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description, TaskType.EVENT);
        assert from != null : "An event must have a parsed start date and time";
        assert to != null : "An event must have a parsed end date and time";
        if (!to.isAfter(from)) {
            throw new IllegalArgumentException(INVALID_DATE_RANGE_MESSAGE);
        }
        this.from = from;
        this.to = to;
    }

    /** Returns the event start date and time. */
    public LocalDateTime getFrom() {
        return from;
    }

    /** Returns the event end date and time. */
    public LocalDateTime getTo() {
        return to;
    }

    /** Returns whether another event has the same start and end date-times. */
    @Override
    protected boolean hasSameSchedule(Task other) {
        return other instanceof Event event
                && from.equals(event.from)
                && to.equals(event.to);
    }

    /** Returns the user-facing representation including the event range. */
    @Override
    public String toString() {
        return super.toString() + " (from: " + Pulbot.formatDateTime(from)
                + " to: " + Pulbot.formatDateTime(to) + ")";
    }
}
