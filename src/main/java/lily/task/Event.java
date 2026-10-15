package lily.task;

import java.time.LocalDateTime;

import lily.parser.DateTimeParser;
import lily.storage.Storage;

/** Represents a task that occurs during a time interval. */
public class Event extends Task {
    protected LocalDateTime from;
    protected LocalDateTime to;
    private final boolean hasExplicitFromTime;
    private final boolean hasExplicitToTime;

    /**
     * Creates an event with the given description and time range.
     *
     * @param description description of the event
     * @param from        start date and time
     * @param to          end date and time
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        this(description, from, !from.toLocalTime().equals(java.time.LocalTime.MIDNIGHT),
                to, !to.toLocalTime().equals(java.time.LocalTime.MIDNIGHT));
    }

    /** Creates an event while retaining whether each endpoint's time was explicitly entered. */
    public Event(String description, LocalDateTime from, boolean hasExplicitFromTime,
            LocalDateTime to, boolean hasExplicitToTime) {
        super(description);
        // The command parser establishes this interval invariant before creating the event.
        assert from != null && to != null : "An event must have both endpoints";
        assert !to.isBefore(from) : "An event cannot end before it starts";
        this.from = from;
        this.to = to;
        this.hasExplicitFromTime = hasExplicitFromTime;
        this.hasExplicitToTime = hasExplicitToTime;
    }

    /** Returns the event's start date and time. */
    public LocalDateTime getFrom() {
        return from;
    }

    /** Returns the event's end date and time. */
    public LocalDateTime getTo() {
        return to;
    }

    /** Returns whether the user explicitly supplied the event start time. */
    public boolean hasExplicitFromTime() {
        return hasExplicitFromTime;
    }

    /** Returns whether the user explicitly supplied the event end time. */
    public boolean hasExplicitToTime() {
        return hasExplicitToTime;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: "
                + DateTimeParser.formatForDisplay(this.from, hasExplicitFromTime)
                + " to: " + DateTimeParser.formatForDisplay(this.to, hasExplicitToTime) + ")";
    }

    @Override
    public String toFileString() {
        return "E | " + (isDone ? "1" : "0") + " | " + Storage.escapeField(description)
                + " | " + DateTimeParser.formatForStorage(from)
                + " | " + DateTimeParser.formatForStorage(to)
                + " | " + hasExplicitFromTime + " | " + hasExplicitToTime;
    }

}
