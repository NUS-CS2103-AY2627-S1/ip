package bro.task;

import java.time.LocalDate;
import java.util.Objects;

import bro.exception.BroException;

/**
 * Represents an event task spanning a specific start and end time.
 * Start and end timestamps are parsed and stored as TaskDateTime objects.
 */
public class Event extends Task {
    private TaskDateTime start;
    private TaskDateTime end;

    /**
     * Constructs an Event task with a description, start time, and end time.
     *
     * @param description The description of the event.
     * @param startStr    The date or date-time string for the start of the event.
     * @param endStr      The date or date-time string for the end of the event.
     * @throws BroException If either date/time string cannot be parsed or if end is before start.
     */
    public Event(String description, String startStr, String endStr) throws BroException {
        super(description);
        TaskDateTime parsedStart = TaskDateTime.parse(startStr);
        TaskDateTime parsedEnd = TaskDateTime.parse(endStr);
        validateChronologicalOrder(parsedStart, parsedEnd);
        this.start = parsedStart;
        this.end = parsedEnd;
    }

    /**
     * Validates that the event's end time does not occur before its start time.
     *
     * @param start The start date/time.
     * @param end   The end date/time.
     * @throws BroException If end is chronologically before start.
     */
    public static void validateChronologicalOrder(TaskDateTime start, TaskDateTime end) throws BroException {
        if (start != null && end != null && end.isBefore(start)) {
            throw new BroException(String.format(
                    "Bro, an event can't end before it even starts.\nStart: %s\nEnd: %s",
                    start.formatDisplay(), end.formatDisplay()));
        }
    }

    /**
     * Checks if this event occurs on or spans across the specified date.
     *
     * @param date The LocalDate to check against.
     * @return True if the event spans across or includes the specified date, false
     *         otherwise.
     */
    @Override
    public boolean isOnDate(LocalDate date) {
        LocalDate startDate = this.start.toLocalDate();
        LocalDate endDate = this.end.toLocalDate();

        if (startDate != null && endDate != null) {
            return !date.isBefore(startDate) && !date.isAfter(endDate);
        } else if (startDate != null) {
            return startDate.isEqual(date);
        } else if (endDate != null) {
            return endDate.isEqual(date);
        }
        return false;
    }

    /**
     * Gets the TaskDateTime start object.
     *
     * @return The TaskDateTime instance for start.
     */
    public TaskDateTime getStart() {
        return this.start;
    }

    /**
     * Sets the start date and time.
     *
     * @param start The new TaskDateTime instance for start.
     */
    public void setStart(TaskDateTime start) {
        this.start = start;
    }

    /**
     * Sets the start date and time from a date-time string.
     *
     * @param startStr The date or date-time string for the start of the event.
     * @throws BroException If the date/time string cannot be parsed.
     */
    public void setStart(String startStr) throws BroException {
        this.start = TaskDateTime.parse(startStr);
    }

    /**
     * Gets the TaskDateTime end object.
     *
     * @return The TaskDateTime instance for end.
     */
    public TaskDateTime getEnd() {
        return this.end;
    }

    /**
     * Sets the end date and time.
     *
     * @param end The new TaskDateTime instance for end.
     */
    public void setEnd(TaskDateTime end) {
        this.end = end;
    }

    /**
     * Sets the end date and time from a date-time string.
     *
     * @param endStr The date or date-time string for the end of the event.
     * @throws BroException If the date/time string cannot be parsed.
     */
    public void setEnd(String endStr) throws BroException {
        this.end = TaskDateTime.parse(endStr);
    }

    /**
     * Returns a string representation of the Event task for display.
     *
     * @return Formatted string including task type, status icon, description, and
     *         formatted start/end dates.
     */
    @Override
    public String toString() {
        return String.format("[E]%s (from: %s to: %s)", super.toString(),
                this.start.formatDisplay(), this.end.formatDisplay());
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        Event other = (Event) obj;
        return Objects.equals(this.start, other.start) && Objects.equals(this.end, other.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), start, end);
    }

    /**
     * Returns a string representation of the Event task formatted for file storage.
     *
     * @return Formatted storage string with pipe delimiters.
     */
    @Override
    public String toFileFormat() {
        return formatFilePrefix("E") + " | " + this.start.formatFile() + " | " + this.end.formatFile();
    }
}
