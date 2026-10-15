package koko;

import java.time.LocalDateTime;

/**
 * Represents a task that occurs between a user-provided start and end time.
 */
public class Event extends Task {
    /** The event's start date and time. */
    private final LocalDateTime from;

    /** The event's end date and time. */
    private final LocalDateTime to;

    /**
     * Creates an event task.
     *
     * @param description the text describing the event
     * @param from the event's start date and time
     * @param to the event's end date and time
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        assert from != null && to != null : "An event must have both a start and end date and time.";
        assert !to.isBefore(from) : "An event cannot end before it starts.";
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event's start date and time.
     *
     * @return the event's start date and time
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns the event's end date and time.
     *
     * @return the event's end date and time
     */
    public LocalDateTime getTo() {
        return to;
    }

    /**
     * Returns this event in the task-list format.
     *
     * @return the formatted event
     */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + from.format(DISPLAY_DATE_TIME_FORMAT)
                + " to: " + to.format(DISPLAY_DATE_TIME_FORMAT) + ")";
    }
}
