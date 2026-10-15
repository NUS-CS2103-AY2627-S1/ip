package tardt.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import tardt.exception.TardTException;
import tardt.priority.Priority;

/**
 * A type of Task that has a start and end time associated with it.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd uuuu HH:mm",
            Locale.ENGLISH);
    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Constructor for Event object
     * @param description Description of the event task
     * @param from Start date and time
     * @param to End date and time
     * @throws TardTException Unique exception of TardT class
     */
    public Event(String description, String from, String to) throws TardTException {
        this(description, from, to, Priority.LOW);
    }

    /** Creates an event task with the given priority. */
    public Event(String description, String from, String to, Priority priority) throws TardTException {
        super(description, priority);
        try {
            this.from = DateTimeParser.parse(from);
            this.to = DateTimeParser.parse(to);
            validateTimeRange();
        } catch (DateTimeParseException e) {
            throw new TardTException(DateTimeParser.FORMAT_ERROR);
        }
    }

    /**
     * Outputs the start date and time in a more readable format.
     * For example: Oct 12 2026 12:00
     *
     * @return A String in the following form: {shortname of month} {day} {year} {time}.
     */
    public String getFrom() {
        return from.format(DISPLAY_FORMAT);
    }

    /**
     * Outputs the end date and time in a more readable format.
     * For example: Oct 12 2026 12:00
     *
     * @return A String in the following form: {shortname of month} {day} {year} {time}.
     */
    public String getTo() {
        return to.format(DISPLAY_FORMAT);
    }

    /**
     * Outputs the start date and time in ISO-8601 format
     *
     * @return A String representing date-time in ISO-8601 format.
     */
    public String getFromRaw() {
        return from.toString();
    }

    /**
     * Outputs the end date and time in ISO-8601 format
     *
     * @return A String representing date-time in ISO-8601 format.
     */
    public String getToRaw() {
        return to.toString();
    }

    /**
     * Ensures an event has a positive duration.
     *
     * @throws TardTException if the end is not after the start
     */
    private void validateTimeRange() throws TardTException {
        if (!from.isBefore(to)) {
            throw new TardTException("Event end time must be after its start time.");
        }
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + this.getFrom() + " to: " + this.getTo() + ")";
    }
}
