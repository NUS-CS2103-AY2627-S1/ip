package glennon.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that occurs between specified start and end date-times.
 */
public class Event extends Task {
    /** Format used when showing event boundaries to the user. */
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d uuuu, h:mm a", Locale.ENGLISH);

    /** Format used when showing all-day event boundaries. */
    private static final DateTimeFormatter DATE_DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d uuuu", Locale.ENGLISH);

    /** Date and time when the event starts. */
    private final LocalDateTime startDateTime;

    /** Date and time when the event ends. */
    private final LocalDateTime endDateTime;

    /**
     * Creates a pending event task with the given description and time range.
     *
     * @param description description of the event.
     * @param startDateTime date and time when the event starts.
     * @param endDateTime date and time when the event ends.
     * @throws IllegalArgumentException if the description is invalid, either date-time is null,
     *         or the end is not after the start.
     */
    public Event(String description, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        super(description);
        if (startDateTime == null || endDateTime == null) {
            throw new IllegalArgumentException("Event start and end date-times must not be null.");
        }
        if (!endDateTime.isAfter(startDateTime)) {
            throw new IllegalArgumentException("Event end must be after its start.");
        }
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    /**
     * Checks for the same concrete type, description, and event boundaries,
     * ignoring completion status.
     *
     * @param other task to compare with this event.
     * @return true when both events have the same defining details.
     */
    @Override
    public boolean hasSameDetails(Task other) {
        return other instanceof Event event
                && super.hasSameDetails(other)
                && startDateTime.equals(event.startDateTime)
                && endDateTime.equals(event.endDateTime);
    }

    /**
     * Returns the event's start date and time.
     *
     * @return event start date and time.
     */
    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    /**
     * Returns the event's end date and time.
     *
     * @return event end date and time.
     */
    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    /**
     * Checks whether any part of this event occurs on the given date. Both the
     * start and end dates are included for events spanning multiple days.
     *
     * @param date date to check.
     * @return true when the event overlaps that date.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        LocalDate startDate = startDateTime.toLocalDate();
        LocalDate endDate = endDateTime.toLocalDate();
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Returns the task with its type, completion status, and time range.
     *
     * @return display-ready text such as
     *         {@code [E][ ] meeting (from: Dec 2 2019, 2:00 PM
     *         to: Dec 2 2019, 4:00 PM)}.
     */
    @Override
    public String toString() {
        if (startDateTime.toLocalTime().equals(LocalTime.MIN)
                && endDateTime.toLocalTime().equals(LocalTime.MAX)) {
            return "[E]" + super.toString()
                    + " (all day: " + startDateTime.toLocalDate().format(DATE_DISPLAY_FORMAT)
                    + " to: " + endDateTime.toLocalDate().format(DATE_DISPLAY_FORMAT) + ")";
        }
        return "[E]" + super.toString()
                + " (from: " + startDateTime.format(DISPLAY_FORMAT)
                + " to: " + endDateTime.format(DISPLAY_FORMAT) + ")";
    }
}
