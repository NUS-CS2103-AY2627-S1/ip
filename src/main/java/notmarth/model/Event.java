package notmarth.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import notmarth.parser.DateTimeParser;

/**
 * Represents a task that takes place between a start and an end date or time.
 */
public class Event extends Task {
    private final LocalDateTime from;
    private final LocalDateTime to;
    private final boolean hasStartTime;
    private final boolean hasEndTime;

    /**
     * Creates an unfinished event task from supported date or time text.
     *
     * @param description the task description.
     * @param from the date or time when the event starts.
     * @param to the date or time when the event ends.
     * @throws DateTimeParseException if either endpoint is not supported.
     * @throws IllegalArgumentException if the event ends before it starts.
     */
    public Event(String description, String from, String to) {
        super(description, TaskType.EVENT);
        DateTimeParser.ParsedDateTime parsedFrom = DateTimeParser.parse(from);
        DateTimeParser.ParsedDateTime parsedTo = DateTimeParser.parse(to);
        this.from = parsedFrom.value();
        this.to = parsedTo.value();
        this.hasStartTime = parsedFrom.hasTime();
        this.hasEndTime = parsedTo.hasTime();
        validateRange();
    }

    /**
     * Creates an event from already parsed date and time values.
     *
     * @param description the event description.
     * @param from the event start date and time.
     * @param to the event end date and time.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description, TaskType.EVENT);
        if (from == null || to == null) {
            throw new IllegalArgumentException("An event needs both a start and end date or time.");
        }
        this.from = from;
        this.to = to;
        this.hasStartTime = true;
        this.hasEndTime = true;
        validateRange();
    }

    /**
     * Creates a date-only event from already parsed dates.
     *
     * @param description the event description.
     * @param from the event start date.
     * @param to the event end date.
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description, TaskType.EVENT);
        if (from == null || to == null) {
            throw new IllegalArgumentException("An event needs both a start and end date.");
        }
        this.from = from.atStartOfDay();
        this.to = to.atStartOfDay();
        this.hasStartTime = false;
        this.hasEndTime = false;
        validateRange();
    }

    /**
     * Returns the typed event start value.
     *
     * @return the event start date and time, at midnight for a date-only value.
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns the typed event end value.
     *
     * @return the event end date and time, at midnight for a date-only value.
     */
    public LocalDateTime getTo() {
        return to;
    }

    /**
     * Returns the event start value in the task archive format.
     *
     * @return an ISO date or ISO local date-time.
     */
    public String getFromForStorage() {
        return DateTimeParser.formatForStorage(from, hasStartTime);
    }

    /**
     * Returns the event end value in the task archive format.
     *
     * @return an ISO date or ISO local date-time.
     */
    public String getToForStorage() {
        return DateTimeParser.formatForStorage(to, hasEndTime);
    }

    /** Checks whether another task has the same description and event range. */
    @Override
    public boolean hasSameDetailsAs(Task other) {
        return other instanceof Event event
                && super.hasSameDetailsAs(other)
                && getFromForStorage().equals(event.getFromForStorage())
                && getToForStorage().equals(event.getToForStorage());
    }

    /**
     * Checks whether this event is in progress on a calendar date. An event
     * spanning multiple dates matches every date in its inclusive range.
     *
     * @param date the date to check.
     * @return {@code true} when the event occurs on the date.
     */
    public boolean isOccurringOn(LocalDate date) {
        LocalDate startDate = from.toLocalDate();
        LocalDate endDate = to.toLocalDate();
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Ensures the event's end does not occur before its start.
     *
     * @throws IllegalArgumentException if the event range travels backwards.
     */
    private void validateRange() {
        if (!from.isBefore(to)) {
            throw new IllegalArgumentException("An event must end after it starts.");
        }
    }

    /**
     * Returns this task with its event range and type marker.
     *
     * @return the formatted event task.
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + DateTimeParser.format(from, hasStartTime)
                + " to: " + DateTimeParser.format(to, hasEndTime) + ")";
    }
}
