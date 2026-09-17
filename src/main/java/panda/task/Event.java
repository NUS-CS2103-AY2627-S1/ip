package panda.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;

import panda.exception.task.InvalidEventParametersException;
import panda.util.datetime.DateTimeHelper;

/**
 * Represents a task scheduled between supplied start and end date/time values.
 */
public class Event extends Task {
    private final Temporal startDateTime;
    private final Temporal endDateTime;
    private final LocalDateTime effectiveStartDateTime;
    private final LocalDateTime effectiveEndDateTime;

    /**
     * Creates an unmarked event task.
     *
     * @param description Description of the event.
     * @param startDateTime Event start date and time.
     * @param endDateTime Event end date and time.
     * @throws InvalidEventParametersException If the event does not end after it starts.
     */
    public Event(String description, Temporal startDateTime, Temporal endDateTime)
            throws InvalidEventParametersException {
        super(description);
        assert startDateTime != null : "Event start date and time must not be null";
        assert endDateTime != null : "Event end date and time must not be null";

        effectiveStartDateTime = normalizeStart(startDateTime);
        effectiveEndDateTime = normalizeEnd(endDateTime);
        if (!effectiveStartDateTime.isBefore(effectiveEndDateTime)) {
            throw new InvalidEventParametersException();
        }

        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    private static LocalDateTime normalizeStart(Temporal startDateTime) {
        if (startDateTime instanceof LocalDate date) {
            return date.atStartOfDay();
        }
        if (startDateTime instanceof LocalDateTime dateTime) {
            return dateTime;
        }
        throw new IllegalArgumentException("Unsupported event start type: "
                + startDateTime.getClass().getName());
    }

    private static LocalDateTime normalizeEnd(Temporal endDateTime) {
        if (endDateTime instanceof LocalDate date) {
            return date.plusDays(1).atStartOfDay();
        }
        if (endDateTime instanceof LocalDateTime dateTime) {
            return dateTime;
        }
        throw new IllegalArgumentException("Unsupported event end type: "
                + endDateTime.getClass().getName());
    }

    /**
     * Returns the event's start date and time.
     *
     * @return Event start as a {@link Temporal}.
     */
    public Temporal getStartDateTime() {
        return startDateTime;
    }

    /**
     * Returns the event's end date and time.
     *
     * @return Event end as a {@link Temporal}.
     */
    public Temporal getEndDateTime() {
        return endDateTime;
    }

    /**
     * Returns the effective inclusive start used for schedule comparisons.
     *
     * @return Normalized event start.
     */
    LocalDateTime getEffectiveStartDateTime() {
        return effectiveStartDateTime;
    }

    /**
     * Returns the effective exclusive end used for schedule comparisons.
     *
     * @return Normalized event end.
     */
    LocalDateTime getEffectiveEndDateTime() {
        return effectiveEndDateTime;
    }

    /**
     * Checks if the event occupies any time on the specified date.
     *
     * @param date The date to check against.
     * @return Whether the event's half-open interval overlaps the specified date.
     */
    @Override
    public boolean occursOn(Temporal date) {
        if (date == null) {
            return false;
        }
        LocalDateTime targetStart = LocalDate.from(date).atStartOfDay();
        LocalDateTime targetEnd = targetStart.plusDays(1);
        return effectiveStartDateTime.isBefore(targetEnd)
                && targetStart.isBefore(effectiveEndDateTime);
    }

    /**
     * Returns a string representation of the event.
     *
     * @return Formatted event string.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + DateTimeHelper.formatForDisplay(startDateTime)
                + " to: " + DateTimeHelper.formatForDisplay(endDateTime) + ")";
    }
}
