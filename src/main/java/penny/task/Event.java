package penny.task;

import java.time.LocalDate;

import penny.common.PennyException;
import penny.common.Time;

/**
 * Represents an event task occurring within a specified time window from start to end.
 */
public class Event extends Task {

    /** Single-character type code identifying an event task in storage. */
    public static final String TYPE_CODE = "E";

    /** The start date and optional time of the event. */
    private final Time from;

    /** The end date and optional time of the event. */
    private final Time to;

    /**
     * Constructs an Event task with date/time strings for from and to boundaries.
     *
     * @param description The event description.
     * @param from The start date/time string.
     * @param to The end date/time string.
     * @throws PennyException If any date/time string cannot be parsed.
     */
    public Event(String description, String from, String to) throws PennyException {
        super(description);
        this.from = new Time(from);
        this.to = new Time(to);
    }

    /**
     * Constructs an Event task with pre-parsed Time objects for from and to.
     *
     * @param description The event description.
     * @param from The start Time.
     * @param to The end Time.
     */
    public Event(String description, Time from, Time to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the start Time of the event.
     *
     * @return The start {@link Time}.
     */
    public Time getFrom() {
        return this.from;
    }

    /**
     * Returns the end Time of the event.
     *
     * @return The end {@link Time}.
     */
    public Time getTo() {
        return this.to;
    }

    /**
     * Checks if this event occurs on or spans across the specified date.
     *
     * @param targetDate The date to check against.
     * @return True if targetDate falls within [startDate, endDate], false otherwise.
     */
    @Override
    public boolean isOnDate(LocalDate targetDate) {
        LocalDate startDate = this.from.getDate();
        LocalDate endDate = this.to.getDate();
        return !targetDate.isBefore(startDate) && !targetDate.isAfter(endDate);
    }

    /**
     * Formats the event task into a pipe-delimited string for disk persistence.
     *
     * @return Formatted file storage string (e.g., "E | 0 | meeting | 2019-10-15 1400 | 2019-10-15 1600").
     */
    @Override
    public String toFileFormat() {
        return toFileFormatPrefix(TYPE_CODE) + FIELD_DELIMITER + this.from.toFileFormat()
                + FIELD_DELIMITER + this.to.toFileFormat();
    }

    /**
     * Returns the string representation of the event task for display.
     *
     * @return Formatted event string (e.g., "[E][ ] meeting (from: Oct 15 2019, 2:00pm to: Oct 15 2019, 4:00pm)").
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + this.from.toString() + " to: " + this.to.toString() + ")";
    }
}
