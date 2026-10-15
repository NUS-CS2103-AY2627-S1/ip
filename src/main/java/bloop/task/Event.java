package bloop.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that occurs over a specified time period.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("MMM dd uuuu HH:mm");
    protected LocalDateTime from;
    protected LocalDateTime to;

    /**
     * Creates an event with a description, start time, and end time.
     *
     * @param description description of the event
     * @param from start date or time
     * @param to end date or time
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /** Creates an event with the given description, time range, and priority. */
    public Event(String description, LocalDateTime from, LocalDateTime to, Priority priority) {
        super(description, priority);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event's start date or time.
     *
     * @return start date or time
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns the event's end date or time.
     *
     * @return end date or time
     */
    public LocalDateTime getTo() {
        return to;
    }

    /** Returns whether the supplied date falls within this event's inclusive date range. */
    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(from.toLocalDate()) && !date.isAfter(to.toLocalDate());
    }

    /** Returns a display representation containing the formatted start and end times. */
    @Override
    public String toString() {
        return appendPriority("[E][" + getStatusIcon() + "] " + description
                + " (from: " + from.format(DISPLAY_DATE_TIME_FORMAT)
                + " to: " + to.format(DISPLAY_DATE_TIME_FORMAT) + ")");
    }
}
