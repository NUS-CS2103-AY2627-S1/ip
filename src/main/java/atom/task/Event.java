package atom.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import atom.exception.AtomException;
import atom.exception.AtomImpossibleDateException;
import atom.exception.AtomInvalidDateException;
import atom.exception.AtomInvalidEventTimeException;

/**
 * Represents a task with a start and end time.
 */
public class Event extends Task {
    private static final String DATE_TIME_FORMAT = "MMM d yyyy, h:mm a";
    private static final DateTimeFormatter INPUT_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm")
                    .withResolverStyle(ResolverStyle.STRICT);

    /** The start time of the event */
    private final LocalDateTime startTime;

    /** The end time of the event */
    private final LocalDateTime endTime;

    /**
     * Instantiates an <code>Event</code> task.
     *
     * @param description The description of the task.
     * @param startTime The starting time of the task.
     * @param endTime The ending time of the task.
     */
    public Event(String description, String startTime, String endTime) {
        super(description);
        try {
            this.startTime = parseDateTime(startTime);
            this.endTime = parseDateTime(endTime);
        } catch (DateTimeParseException e) {
            throw new AtomInvalidDateException();
        }
        if (!this.startTime.isBefore(this.endTime)) {
            throw new AtomInvalidEventTimeException();
        }
    }

    /**
     * Parses a timestamp, retaining support for date-only values.
     *
     * @param dateTime The date and time arguments.
     */
    private static LocalDateTime parseDateTime(String dateTime) {
        try {
            return LocalDateTime.parse(dateTime, INPUT_FORMAT);
        } catch (DateTimeParseException e) {
            return parseDateOnly(dateTime);
        }
    }

    /**
     * Parses a date-only input and provides a specific error for impossible dates.
     *
     * @param dateTime The date input.
     * @return The parsed date at midnight.
     */
    private static LocalDateTime parseDateOnly(String dateTime) {
        try {
            return LocalDate.parse(dateTime).atTime(LocalTime.MIDNIGHT);
        } catch (DateTimeParseException e) {
            throw getDateParseException(dateTime);
        }
    }

    /**
     * Creates the appropriate exception for an invalid date input.
     *
     * @param dateTime The invalid date input.
     * @return The appropriate date parsing exception.
     */
    private static AtomException getDateParseException(String dateTime) {
        if (dateTime.matches("\\d{4}-\\d{2}-\\d{2}( \\d{2}:\\d{2})?")) {
            return new AtomImpossibleDateException();
        }
        return new AtomInvalidDateException();
    }

    /** @return The start date and time of this event. */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /** @return The end date and time of this event. */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return String.format("[E]%s (from: %s to: %s)",
                super.toString(),
                startTime.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)),
                endTime.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
    }
}
