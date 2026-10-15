package ralph.model;

import java.time.LocalDateTime;

/**
 * An Event task: stores start and end as java.time.LocalDateTime.
 */
public class Event extends Task {
    protected LocalDateTime from;
    protected LocalDateTime to;

    /**
     * Constructs an Event with start and end times.
     *
     * @param description short description of the event
     * @param from        start date/time
     * @param to          end date/time
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        assert from != null : "Event start time must not be null";
        assert to != null : "Event end time must not be null";
        assert !to.isBefore(from) : "Event end time must not be before the start time";
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the start date/time of the event.
     *
     * @return start LocalDateTime
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns the end date/time of the event.
     *
     * @return end LocalDateTime
     */
    public LocalDateTime getTo() {
        return to;
    }

    /**
     * Returns a user-friendly string representation of the event including formatted dates/times.
     *
     * @return formatted string for display
     */
    @Override
    public String toString() {
        String fromStr = DateTimeUtil.formatDateOrDateTime(from);
        String toStr = DateTimeUtil.formatDateOrDateTime(to);
        return "[E]" + super.toString() + " (from: " + fromStr + " to: " + toStr + ")";
    }
}
