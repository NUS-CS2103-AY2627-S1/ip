package bingus.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that starts and ends at specified times.
 */
public class Event extends Task {

    /** Formatter used to present event times in task output. */
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("MMM d uuuu, h:mm a", Locale.ENGLISH);

    /** Date and time at which this event begins. */
    private final LocalDateTime from;

    /** Date and time at which this event ends. */
    private final LocalDateTime to;

    /**
     * Creates an event task.
     *
     * @param description description of the event.
     * @param from date and time at which the event starts.
     * @param to date and time at which the event ends.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description, TaskType.EVENT);

        assert from != null : "Event start time must not be null";
        assert to != null : "Event end time must not be null";
        assert from.isBefore(to) : "Event end time must be after its start time";

        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event's start date and time.
     *
     * @return start date and time.
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns the event's end date and time.
     *
     * @return end date and time.
     */
    public LocalDateTime getTo() {
        return to;
    }

    /**
     * Returns whether this event overlaps the specified calendar date.
     *
     * @param date date to check.
     * @return true if the event occurs on the date.
     */
    @Override
    public boolean matchesDate(LocalDate date) {
        LocalDate startDate = from.toLocalDate();
        LocalDate endDate = to.toLocalDate();
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Returns this event in the format used for display.
     *
     * @return formatted event task text.
     */
    @Override
    public String getDisplayString() {
        return super.getDisplayString() + " (from: " + from.format(DISPLAY_DATE_TIME_FORMAT)
                + " to: " + to.format(DISPLAY_DATE_TIME_FORMAT) + ")";
    }
}
