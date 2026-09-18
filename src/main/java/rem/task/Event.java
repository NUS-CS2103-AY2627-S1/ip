package rem.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A task that happens between a start and end date/time.
 */
public class Event extends Task {
    protected LocalDateTime from;
    protected LocalDateTime to;

    /**
     * Creates an event.
     *
     * @param description Description of the event.
     * @param from Date and time at which the event starts.
     * @param to Date and time at which the event ends.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        assert from != null : "Event start date must not be null";
        assert to != null : "Event end date must not be null";
        assert to.isAfter(from) : "Event must end after it starts";
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the date and time at which this event starts.
     *
     * @return Start date and time.
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns the date and time at which this event ends.
     *
     * @return End date and time.
     */
    public LocalDateTime getTo() {
        return to;
    }

    /**
     * Checks whether this event overlaps a given date.
     *
     * @param date Date to check.
     * @return True if the event occurs at any time on that date.
     */
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(from.toLocalDate()) && !date.isAfter(to.toLocalDate());
    }

    /**
     * Returns this event's completion status, description, time range, and optional note.
     *
     * @return Display representation of this event.
     */
    @Override
    public String toString() {
        return formatWithNote("[E]" + getTaskDetails() + " (from: " + TaskDateTime.format(from)
                + " to: " + TaskDateTime.format(to) + ")");
    }
}
