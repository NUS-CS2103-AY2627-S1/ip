package yapatron.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import yapatron.YapException;

/**
 * Represents an event with a start and end time.
 */
public class Event extends Task {
    private static final String DATE_PATTERN =
            "\\d{1,2}/\\d{1,2}/\\d{4}";
    private static final String TIME_PATTERN =
            "\\d{4}";
    private static final String DATE_TIME_PATTERN =
            DATE_PATTERN + "\\s+" + TIME_PATTERN;

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("d/M/uuuu")
                    .withResolverStyle(ResolverStyle.STRICT);

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("HHmm")
                    .withResolverStyle(ResolverStyle.STRICT);

    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm")
                    .withResolverStyle(ResolverStyle.STRICT);

    /** String for starting point. */
    protected String from;

    /** String for end point. */
    protected String to;

    /**
     * Creates an Event instance with a description, start, and end point.
     *
     * @param desc event description
     * @param from event start date or time
     * @param to event end date or time
     * @throws YapException if a value is empty, invalid, or out of order
     */
    public Event(String desc, String from, String to) throws YapException {
        super(requireText(desc, "EVENT description"));

        this.from = requireText(from, "EVENT start time");
        this.to = requireText(to, "EVENT end time");

        if (this.from.equalsIgnoreCase(this.to)) {
            throw new YapException(
                    "Oops!!! EVENT start and end times must be different.");
        }

        validateDateTimeOrder(this.from, this.to);
    }

    /**
     * Ensures that a value entered isn't null or blank.
     *
     * @param value value to check
     * @param field name of the field being checked
     * @return value with no space
     * @throws YapException if the value is empty
     */
    private static String requireText(String value, String field)
            throws YapException {
        if (value == null || value.trim().isEmpty()) {
            throw new YapException("Woah!!! " + 
                    field + " cannot be empty.");
        }

        return value.trim();
    }

    /**
     * Ensures the event values and their ordering are valid
     *
     * @param from event start time
     * @param to event end time
     * @throws YapException if either value is invalid or out of order
     */
    private static void validateDateTimeOrder(String from, String to)
            throws YapException {
        validateDateOrTime(from, "EVENT start time");
        validateDateOrTime(to, "EVENT end time");

        try {
            if (from.matches(DATE_TIME_PATTERN)
                    && to.matches(DATE_TIME_PATTERN)) {

                LocalDateTime start =
                        LocalDateTime.parse(from, DATE_TIME_FORMAT);
                LocalDateTime end =
                        LocalDateTime.parse(to, DATE_TIME_FORMAT);

                if (!start.isBefore(end)) {
                    throw new YapException(
                            "Oops!!! EVENT start date/time must be earlier "
                                    + "than the end date/time.");
                }

            } else if (from.matches(DATE_PATTERN)
                    && to.matches(DATE_PATTERN)) {

                LocalDate start = LocalDate.parse(from, DATE_FORMAT);
                LocalDate end = LocalDate.parse(to, DATE_FORMAT);

            } else if (from.matches(TIME_PATTERN)
                    && to.matches(TIME_PATTERN)) {

                LocalTime start = LocalTime.parse(from, TIME_FORMAT);
                LocalTime end = LocalTime.parse(to, TIME_FORMAT);

                if (!start.isBefore(end)) {
                    throw new YapException(
                            "Oops!!! EVENT start time must be earlier "
                                    + "than the end time.");
                }
            }

        } catch (DateTimeParseException e) {
            throw new YapException(
                    "You think you can fool me?? " +
                    "EVENT values must contain valid dates or times.");
        }
    }

    /**
     * Checks if a value contains a valid date-time value.
     *
     * @param value value to validate
     * @param field name of the field being checked
     * @throws YapException if the value is invalid
     */
    private static void validateDateOrTime(String value, String field)
            throws YapException {
        boolean isDate = value.matches(DATE_PATTERN);
        boolean isTime = value.matches(TIME_PATTERN);
        boolean isDateTime = value.matches(DATE_TIME_PATTERN);

        if (!isDate && !isTime && !isDateTime) {
            throw new YapException(
                    "Woah! " + field + " must contain a date or time. "
                            + "Use d/M/yyyy, HHmm, or d/M/yyyy HHmm.");
        }

        try {
            if (isDateTime) {
                LocalDateTime.parse(value, DATE_TIME_FORMAT);
            } else if (isDate) {
                LocalDate.parse(value, DATE_FORMAT);
            } else {
                LocalTime.parse(value, TIME_FORMAT);
            }
        } catch (DateTimeParseException e) {
            throw new YapException("Woah!!! " + 
                    field + " must contain a valid date or time.");
        }
    }

    /**
     * Returns the event as text.
     *
     * @return formatted event description
     */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + this.from + " to: " + this.to + ")";
    }

    /**
     * Formats the event for file storage.
     *
     * @return pipe-delimited event data
     */
    @Override
    public String toFileFormat() {
        return "[EVENT] | " + super.toFileFormat()
                + " | " + this.from + " | " + this.to;
    }
}
