package thomas.task;

import java.time.LocalDate;
import java.time.LocalTime;

import thomas.ThomasException;
import thomas.util.DateTimeUtil;

/**
 * Represents a task that occurs within a start and end date/time.
 */
public class Event extends Task {
    protected LocalDate fromDate;
    protected LocalTime fromTime;
    protected LocalDate toDate;
    protected LocalTime toTime;

    /**
     * Creates an event with the given description, start date/time, and end date/time.
     *
     * @param description Textual description of the task.
     * @param from Start date/time.
     * @param to End date/time.
     * @throws ThomasException If a date format is invalid or the end is before the start.
     */
    public Event(String description, String from, String to) throws ThomasException {
        super(description);
        DateTimeUtil.ParsedDateTime parsedFrom = DateTimeUtil.parseDateTime(from);
        DateTimeUtil.ParsedDateTime parsedTo = DateTimeUtil.parseDateTime(to);

        this.fromDate = parsedFrom.getDate();
        this.fromTime = parsedFrom.getTime();
        this.toDate = parsedTo.getDate();
        this.toTime = parsedTo.getTime();

        assert fromDate != null : "A parsed event must have a start date";
        assert toDate != null : "A parsed event must have an end date";
        boolean hasInvalidDateOrder = toDate.isBefore(fromDate);
        boolean hasInvalidSameDayOrder = toDate.equals(fromDate)
                && (fromTime == null || toTime == null || !toTime.isAfter(fromTime));
        if (hasInvalidDateOrder || hasInvalidSameDayOrder) {
            throw new ThomasException("Event end date/time must be after its start date/time.");
        }
    }

    public LocalDate getFromDate() {
        return this.fromDate;
    }

    /**
     * Retrieves the optional start time.
     *
     * @return the start time, or {@code null} if only a date was given
     */
    public LocalTime getFromTime() {
        return this.fromTime;
    }

    /**
     * Retrieves the end date.
     *
     * @return The end LocalDate instance of the event.
     * */
    public LocalDate getToDate() {
        return this.toDate;
    }

    /**
     * Retrieves the optional end time.
     *
     * @return the end time, or {@code null} if only a date was given
     */
    public LocalTime getToTime() {
        return this.toTime;
    }

    /**
     * Returns a formatted string for file storage.
     *
     * @return Formatted string representing this event task.
     */
    @Override
    public String toFileFormat() {
        return "E | " + super.toFileFormat() + " | "
                + DateTimeUtil.toStorageString(fromDate, fromTime)
                + " | "
                + DateTimeUtil.toStorageString(toDate, toTime);
    }

    /**
     * Checks whether the given date falls within this event's start and end dates.
     *
     * @param date date to compare against
     * @return {@code true} if the date is between start and end, inclusive
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return !(date.isBefore(fromDate) || date.isAfter(toDate));
    }

    /**
     * Returns the status, description, start, and end date/time of this event.
     *
     * @return Status icon, description, start, and end date/time as a string.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + DateTimeUtil.toDisplayString(fromDate, fromTime)
                + " to: " + DateTimeUtil.toDisplayString(toDate, toTime) + ")";
    }
}
