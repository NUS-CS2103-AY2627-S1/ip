package echo.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

import echo.EchoException;

/**
 * Parses supported task dates and formats them for display and storage.
 */
public final class DateTimeParser {
    /** Format accepted for a date without a time. */
    public static final String DATE_FORMAT = "dd-mm-yyyy";

    /** Format accepted for an optional 24-hour time. */
    public static final String TIME_FORMAT = "HH:MM";

    /** Full format accepted when a time is supplied. */
    public static final String DATE_TIME_FORMAT = DATE_FORMAT + " " + TIME_FORMAT;

    /** Pattern for a date in the supported numeric format. */
    private static final String DATE_PATTERN = "[0-9]{2}-[0-9]{2}-[0-9]{4}";
    /** Pattern for a date followed by a 24-hour time. */
    private static final String DATE_TIME_PATTERN = DATE_PATTERN + " [0-9]{2}:[0-9]{2}";
    /** Strict formatter for dates received from user input. */
    private static final DateTimeFormatter INPUT_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-uuuu")
                    .withResolverStyle(ResolverStyle.STRICT);
    /** Strict formatter for date-time values received from user input. */
    private static final DateTimeFormatter INPUT_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm")
                    .withResolverStyle(ResolverStyle.STRICT);
    /** Formatter for times written to task storage. */
    private static final DateTimeFormatter STORAGE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);
    /** Formatter for dates shown in the task list. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM d uuuu", Locale.ENGLISH);
    /** Formatter for times shown in the task list. */
    private static final DateTimeFormatter DISPLAY_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("h:mm", Locale.ENGLISH);

    private DateTimeParser() {
    }

    /**
     * Validates a date with an optional 24-hour time.
     *
     * @param value date, or date and time, entered by the user.
     * @throws DateTimeParseException if the value has an unsupported shape or is invalid.
     */
    public static void validate(String value) throws DateTimeParseException {
        parse(value);
    }

    /**
     * Validates that the event start date-time strictly precedes the end date-time.
     *
     * @param start start date and optional time.
     * @param end end date and optional time.
     * @throws EchoException if the start date-time is later than or identical to the end date-time.
     */
    public static void validateChronologicalOrder(DateTimeValue start, DateTimeValue end)
            throws EchoException {
        if (start.date().isAfter(end.date())) {
            throw new EchoException("Event start date (" + start.formatForDisplay()
                    + ") cannot be after end date (" + end.formatForDisplay() + ").");
        }

        if (start.date().isEqual(end.date())) {
            if (start.time() == null && end.time() == null) {
                throw new EchoException("Event start date (" + start.formatForDisplay()
                        + ") cannot be identical to end date without specifying distinct start and end times.");
            }
            if (start.time() != null && end.time() != null) {
                if (start.time().isAfter(end.time())) {
                    throw new EchoException("Event start time (" + start.formatForDisplay()
                            + ") cannot be after end time (" + end.formatForDisplay() + ").");
                }
                if (start.time().equals(end.time())) {
                    throw new EchoException("Event start time (" + start.formatForDisplay()
                            + ") cannot be the same as end time.");
                }
            } else {
                throw new EchoException("When an event occurs on the same date, both start and end "
                        + "must specify times, and start must be before end.");
            }
        }
    }

    /**
     * Parses a date with an optional 24-hour time into Java time values.
     *
     * @param value date, or date and time, entered by the user.
     * @return parsed date and optional time.
     * @throws DateTimeParseException if the value has an unsupported shape or is invalid.
     */
    public static DateTimeValue parse(String value) throws DateTimeParseException {
        if (value.matches(DATE_TIME_PATTERN)) {
            LocalDateTime dateTime = LocalDateTime.parse(value, INPUT_DATE_TIME_FORMATTER);
            return new DateTimeValue(dateTime.toLocalDate(), dateTime.toLocalTime());
        }
        if (value.matches(DATE_PATTERN)) {
            return new DateTimeValue(LocalDate.parse(value, INPUT_DATE_FORMATTER), null);
        }
        throw new DateTimeParseException("Unsupported date-time format", value, 0);
    }

    /**
     * Formats a date and optional time for display in the task list.
     *
     * @param date date to format.
     * @param time optional time to format.
     * @return formatted date, optionally followed by the formatted time.
     */
    public static String formatForDisplay(LocalDate date, LocalTime time) {
        String dateText = formatDate(date);
        return time == null ? dateText : dateText + ", " + formatTime(time);
    }

    /**
     * Formats a date and optional time in the canonical persistence format.
     *
     * @param date date to format.
     * @param time optional time to format.
     * @return date and optional time in the persistence format.
     */
    public static String formatForStorage(LocalDate date, LocalTime time) {
        String dateText = date.format(INPUT_DATE_FORMATTER);
        return time == null ? dateText : dateText + " " + time.format(STORAGE_TIME_FORMATTER);
    }

    /** Formats a date without a time component. */
    private static String formatDate(LocalDate date) {
        return date.format(DISPLAY_DATE_FORMATTER);
    }

    /** Formats a time with a lowercase am or pm suffix. */
    private static String formatTime(LocalTime time) {
        String meridiem = time.getHour() < 12 ? "am" : "pm";
        return time.format(DISPLAY_TIME_FORMATTER) + meridiem;
    }

    /**
     * Holds a parsed date and its optional time component.
     *
     * @param date parsed date.
     * @param time optional time.
     */
    public record DateTimeValue(LocalDate date, LocalTime time) {
        /**
         * Formats this value for display in the task list.
         *
         * @return formatted date, optionally followed by the formatted time.
         */
        public String formatForDisplay() {
            return DateTimeParser.formatForDisplay(date, time);
        }

        /**
         * Formats this value in the canonical persistence format.
         *
         * @return date and optional time in the persistence format.
         */
        public String formatForStorage() {
            return DateTimeParser.formatForStorage(date, time);
        }
    }
}
