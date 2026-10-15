package remy.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a task that occurs over a period.
 */
public class Event extends Task {

    /** Format used when displaying date-only event endpoints. */
    private static final DateTimeFormatter DATE_DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);

    /** Format used when displaying event endpoints that include a time. */
    private static final DateTimeFormatter DATE_TIME_DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd uuuu HH:mm", Locale.ENGLISH);

    /** Date-only start endpoint, when the event has no times. */
    private final LocalDate startDate;

    /** Date-time start endpoint, when the event includes times. */
    private final LocalDateTime startDateTime;

    /** Date-only end endpoint, when the event has no times. */
    private final LocalDate endDate;

    /** Date-time end endpoint, when the event includes times. */
    private final LocalDateTime endDateTime;

    /**
     * Creates an event with date-only endpoints.
     *
     * @param description Description supplied by the user.
     * @param startDate Date on which the event starts.
     * @param endDate Date on which the event ends.
     * @throws IllegalArgumentException if the event does not start before it ends
     */
    public Event(String description, LocalDate startDate, LocalDate endDate) {
        this(description, startDate, endDate, null, null);
    }

    /**
     * Creates an event with date-time endpoints.
     *
     * @param description Description supplied by the user.
     * @param startDateTime Date and time at which the event starts.
     * @param endDateTime Date and time at which the event ends.
     * @throws IllegalArgumentException if the event does not start before it ends
     */
    public Event(String description, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this(description, null, null, startDateTime, endDateTime);
    }

    /**
     * Creates an event with its available endpoint representation.
     *
     * @param description Description supplied by the user.
     * @param startDate Date on which the event starts, if known.
     * @param endDate Date on which the event ends, if known.
     * @param startDateTime Date and time at which the event starts, if known.
     * @param endDateTime Date and time at which the event ends, if known.
     * @throws IllegalArgumentException if the event does not start before it ends
     */
    public Event(String description, LocalDate startDate, LocalDate endDate,
            LocalDateTime startDateTime, LocalDateTime endDateTime) {
        super(description);
        boolean hasDateEndpoints = startDate != null && endDate != null
                && startDateTime == null && endDateTime == null;
        boolean hasDateTimeEndpoints = startDate == null && endDate == null
                && startDateTime != null && endDateTime != null;
        assert hasDateEndpoints || hasDateTimeEndpoints
                : "Event must have exactly one complete endpoint representation";
        boolean hasInvalidDateRange = hasDateEndpoints && !startDate.isBefore(endDate);
        boolean hasInvalidDateTimeRange = hasDateTimeEndpoints && !startDateTime.isBefore(endDateTime);
        if (hasInvalidDateRange || hasInvalidDateTimeRange) {
            throw new IllegalArgumentException("Event start must be before its end");
        }

        this.startDate = startDate;
        this.endDate = endDate;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    /**
     * Returns whether another task is an event with the same description and endpoints.
     *
     * @param otherTask task to compare with
     * @return true when both events have the same identifying details
     */
    @Override
    public boolean hasSameDetailsAs(Task otherTask) {
        if (!(otherTask instanceof Event otherEvent)) {
            return false;
        }
        return super.hasSameDetailsAs(otherEvent)
                && Objects.equals(startDate, otherEvent.startDate)
                && Objects.equals(startDateTime, otherEvent.startDateTime)
                && Objects.equals(endDate, otherEvent.endDate)
                && Objects.equals(endDateTime, otherEvent.endDateTime);
    }

    /**
     * Returns this event's start endpoint as a date-time for chronological sorting.
     *
     * @return start date-time, with a date-only endpoint represented as the start of its day
     */
    @Override
    Optional<LocalDateTime> getChronologicalDateTime() {
        if (startDateTime != null) {
            return Optional.of(startDateTime);
        }
        return Optional.of(startDate.atStartOfDay());
    }

    /**
     * Returns the display representation of an event endpoint.
     *
     * @param date Date-only endpoint, if available.
     * @param dateTime Date-time endpoint, if available.
     * @return The endpoint's display representation.
     */
    private static String formatEndpoint(LocalDate date, LocalDateTime dateTime) {
        if (dateTime != null) {
            return dateTime.format(DATE_TIME_DISPLAY_FORMATTER);
        }
        return date.format(DATE_DISPLAY_FORMATTER);
    }

    /**
     * Returns this event's display representation.
     *
     * @return A string representing this task.
     */
    @Override
    public String toString() {
        String display = "[E]" + super.toString() + " (from: ";

        display += formatEndpoint(startDate, startDateTime);
        display += " to: ";
        display += formatEndpoint(endDate, endDateTime);
        return display + ")";
    }
}
