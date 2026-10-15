package jiji.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jiji.parser.DateTimeUtil;

/**
 * Represents an event task with a start time and an end time.
 */
public class Event extends Task {

    /** Start time or date of the event. */
    private final String from;

    /** End time or date of the event. */
    private final String to;

    /**
     * Constructs a new Event task with the specified description, start time, and end time.
     *
     * @param description Description of the event.
     * @param from Start time or date string.
     * @param to End time or date string.
     */
    public Event(String description, String from, String to) {
        super(description);
        assert from != null && !from.isBlank() : "Event 'from' date/time cannot be null or blank";
        assert to != null && !to.isBlank() : "Event 'to' date/time cannot be null or blank";
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the start LocalDate of this event, or null if not a recognized date.
     *
     * @return The start LocalDate, or null if not applicable.
     */
    public LocalDate getStartDate() {
        LocalDate parsedDate = DateTimeUtil.parseLocalDate(from);
        if (parsedDate != null) {
            return parsedDate;
        }
        LocalDateTime parsedDateTime = DateTimeUtil.parseLocalDateTime(from);
        if (parsedDateTime != null) {
            return parsedDateTime.toLocalDate();
        }
        return null;
    }

    /**
     * Returns the end LocalDate of this event, or null if not a recognized date.
     *
     * @return The end LocalDate, or null if not applicable.
     */
    public LocalDate getEndDate() {
        LocalDate parsedDate = DateTimeUtil.parseLocalDate(to);
        if (parsedDate != null) {
            return parsedDate;
        }
        LocalDateTime parsedDateTime = DateTimeUtil.parseLocalDateTime(to);
        if (parsedDateTime != null) {
            return parsedDateTime.toLocalDate();
        }
        return null;
    }

    /**
     * Checks if this event occurs on the given date.
     * If both start and end dates are known, checks if the date falls within the interval inclusive.
     * If only one date is known, checks if the date matches that known date.
     *
     * @param date The date to check against.
     * @return True if the event takes place on the specified date, false otherwise.
     */
    public boolean occursOn(LocalDate date) {
        if (date == null) {
            return false;
        }
        LocalDate start = getStartDate();
        LocalDate end = getEndDate();
        if (start != null && end != null) {
            return !date.isBefore(start) && !date.isAfter(end);
        } else if (start != null) {
            return date.equals(start);
        } else if (end != null) {
            return date.equals(end);
        }
        return false;
    }

    /**
     * Returns the storage text representation for an Event task with standardized date formatting.
     *
     * @return Formatted string for file storage: {@code "E | <isDone> | <description> | <from> | <to>"}.
     */
    @Override
    public String toFileFormat() {
        return TaskType.EVENT.getCode() + " | " + super.toFileFormat() + " | "
                + DateTimeUtil.formatForStorage(from) + " | " + DateTimeUtil.formatForStorage(to);
    }

    /**
     * Returns the string representation of the event task, prefixed with [E] and suffixing formatted times.
     *
     * @return Formatted event string representation.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + DateTimeUtil.formatForDisplay(from)
                + " to: " + DateTimeUtil.formatForDisplay(to) + ")";
    }

    /**
     * Checks if this event is a duplicate of another task (same description, start, and end parameters).
     *
     * @param other The other task to compare against.
     * @return True if both tasks are events with matching description, from, and to parameters.
     */
    @Override
    public boolean isDuplicateOf(Task other) {
        if (!super.isDuplicateOf(other)) {
            return false;
        }
        Event otherEvent = (Event) other;
        return this.from.trim().equalsIgnoreCase(otherEvent.from.trim())
                && this.to.trim().equalsIgnoreCase(otherEvent.to.trim());
    }

    /**
     * Checks if the event's end date/time is chronologically before its start date/time.
     *
     * @return True if end is before start, or end and start are equal date-times.
     */
    public boolean isEndBeforeStart() {
        LocalDateTime startDateTime = DateTimeUtil.parseLocalDateTime(from);
        LocalDateTime endDateTime = DateTimeUtil.parseLocalDateTime(to);
        if (startDateTime != null && endDateTime != null) {
            return !endDateTime.isAfter(startDateTime);
        }

        LocalDate startDate = getStartDate();
        LocalDate endDate = getEndDate();
        if (startDate != null && endDate != null) {
            return endDate.isBefore(startDate);
        }
        return false;
    }
}
