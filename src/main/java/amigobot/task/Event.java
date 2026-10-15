package amigobot.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that spans a specific time period.
 * Dates can be parsed LocalDates or plain strings (e.g. "Mon 2pm").
 */
public class Event extends Task {

    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy");

    protected LocalDate fromDate;
    protected String fromString;
    protected LocalDate toDate;
    protected String toString;

    /**
     * Creates an Event with parsed dates.
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        this.fromDate = from;
        this.fromString = null;
        this.toDate = to;
        this.toString = null;
    }

    /**
     * Creates an Event with plain string dates.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.fromDate = null;
        this.fromString = from;
        this.toDate = null;
        this.toString = to;
    }

    /**
     * Creates an Event where from and to may independently be dates or strings.
     */
    public Event(String description, LocalDate fromDate, String fromString,
                 LocalDate toDate, String toString) {
        super(description);
        this.fromDate = fromDate;
        this.fromString = fromString;
        this.toDate = toDate;
        this.toString = toString;
    }

    /**
     * Returns the start date as a LocalDate, or null if stored as a string.
     *
     * @return the start date, or null.
     */
    public LocalDate getFromDate() {
        return fromDate;
    }

    /**
     * Returns the start time as a plain string, or null if stored as a LocalDate.
     *
     * @return the start string, or null.
     */
    public String getFromString() {
        return fromString;
    }

    /**
     * Returns the end date as a LocalDate, or null if stored as a string.
     *
     * @return the end date, or null.
     */
    public LocalDate getToDate() {
        return toDate;
    }

    /**
     * Returns the end time as a plain string, or null if stored as a LocalDate.
     *
     * @return the end string, or null.
     */
    public String getToString() {
        return this.toString;
    }

    /**
     * Returns a string representation prefixed with [E] and the time range,
     * e.g. "[E][ ] meeting (from: Mon 2pm to: 4pm)".
     *
     * @return the formatted event string.
     */
    @Override
    public String toString() {
        String fromDisplay = fromDate != null ? fromDate.format(DISPLAY_FORMAT) : fromString;
        String toDisplay = toDate != null ? toDate.format(DISPLAY_FORMAT) : this.toString;
        return "[E]" + super.toString() + " (from: " + fromDisplay + " to: " + toDisplay + ")";
    }
}
