package kpei.datatypes;

import java.time.LocalDate;
import java.time.LocalTime;

import kpei.utility.DateTimeParser;

/**
 * Represents an event task occurring within a specific time period.
 */
public class Event extends Task {
    private LocalDate fromDate;
    private LocalTime fromTime;
    private LocalDate toDate;
    private LocalTime toTime;

    /**
     * Constructs an {@code Event} task with specified completion status, description,
     * start date and time, and end date and time.
     *
     * @param isMarked Whether this event task is marked as completed.
     * @param description The description of the event.
     * @param fromDate The starting date of the event.
     * @param fromTime The starting time of the event.
     * @param toDate The ending date of the event.
     * @param toTime The ending time of the event.
     */
    public Event(boolean isMarked, String description, LocalDate fromDate, LocalTime fromTime,
                 LocalDate toDate, LocalTime toTime) {
        super(isMarked, description);
        this.type = "event";
        this.fromDate = fromDate;
        this.fromTime = fromTime;
        this.toDate = toDate;
        this.toTime = toTime;
    }

    /**
     * Constructs an unmarked {@code Event} task with specified description, start date and time, and end date and time.
     *
     * @param description The description of the event.
     * @param fromDate The starting date of the event.
     * @param fromTime The starting time of the event.
     * @param toDate The ending date of the event.
     * @param toTime The ending time of the event.
     */
    public Event(String description, LocalDate fromDate, LocalTime fromTime, LocalDate toDate, LocalTime toTime) {
        this(false, description, fromDate, fromTime, toDate, toTime);
    }

    @Override
    public String toString() {
        return String.format("%s (from: %s %s, to: %s %s)", super.toString(),
                DateTimeParser.formatDate(fromDate), DateTimeParser.formatTime(fromTime),
                DateTimeParser.formatDate(toDate), DateTimeParser.formatTime(toTime));
    }

    @Override
    public String toFileFormat() {
        return String.format("%s | %s | %s | %s | %s", super.toFileFormat(),
                fromDate.toString(), fromTime.toString(), toDate.toString(), toTime.toString());
    }

    /**
     * Returns the starting date of this event.
     *
     * @return The starting date.
     */
    public LocalDate getFromDate() {
        return fromDate;
    }

    /**
     * Returns the starting time of this event.
     *
     * @return The starting time.
     */
    public LocalTime getFromTime() {
        return fromTime;
    }

    /**
     * Returns the ending date of this event.
     *
     * @return The ending date.
     */
    public LocalDate getToDate() {
        return toDate;
    }

    /**
     * Returns the ending time of this event.
     *
     * @return The ending time.
     */
    public LocalTime getToTime() {
        return toTime;
    }

    /**
     * Returns a formatted string representation of the starting date and time.
     *
     * @return Formatted start date string.
     */
    public String getFormattedFromDate() {
        return String.format("%s %s", DateTimeParser.formatDate(fromDate), DateTimeParser.formatTime(fromTime));
    }

    /**
     * Returns a formatted string representation of the ending date and time.
     *
     * @return Formatted end date string.
     */
    public String getFormattedToDate() {
        return String.format("%s %s", DateTimeParser.formatDate(toDate), DateTimeParser.formatTime(toTime));
    }
}

