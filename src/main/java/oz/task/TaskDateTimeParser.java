package oz.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.util.Locale;

import oz.exception.OzException;

/**
 * Parses supported task date, time, and date-time input formats.
 */
final class TaskDateTimeParser {
    /** Error shown when a date does not exist on the calendar. */
    private static final String NON_EXISTENT_DATE_MESSAGE =
            "That date does not exist on the calendar (e.g., February 30).";

    /** Error shown when a required date-time value is missing. */
    private static final String EMPTY_DATE_TIME_MESSAGE =
            "Date/time argument cannot be empty.";

    /** Error shown when a date-time value has no supported format. */
    private static final String INVALID_DATE_TIME_MESSAGE =
            "Please provide a valid date/time (e.g., 2019-10-15 or 2/12/2019 1800).";

    /** Error shown when a required date value is missing. */
    private static final String EMPTY_DATE_MESSAGE = "Date argument cannot be empty.";

    /** Error shown when a date value has no supported format. */
    private static final String INVALID_DATE_MESSAGE =
            "Please provide a valid date (e.g., 2019-10-15 or 2/12/2019).";

    /** Error shown when a required time value is missing. */
    private static final String EMPTY_TIME_MESSAGE = "Time argument cannot be empty.";

    /** Error shown when a time value has no supported format. */
    private static final String INVALID_TIME_MESSAGE =
            "Please provide a valid time (e.g., 1400, 14:00, or 2pm).";

    /** Supported input formatters for strings containing both date and time. */
    private static final DateTimeFormatter[] INPUT_DATE_TIME_FORMATTERS = {
        createFormatter("uuuu-MM-dd HHmm"),
        createFormatter("d/M/uuuu HHmm"),
        createFormatter("uuuu/M/d HHmm"),
        createFormatter("d-M-uuuu HHmm"),
        createFormatter("uuuu-MM-dd HH:mm"),
        createFormatter("d/M/uuuu HH:mm"),
        createFormatter("uuuu/M/d HH:mm"),
        createFormatter("d-M-uuuu HH:mm"),
        createFormatter("d/M/uuuu h:mma"),
        createFormatter("d/M/uuuu ha"),
        createFormatter("uuuu-MM-dd h:mma"),
        createFormatter("uuuu-MM-dd ha"),
        DateTimeFormatter.ISO_LOCAL_DATE_TIME.withResolverStyle(ResolverStyle.STRICT)
    };

    /** Supported input formatters for strings containing only a date. */
    private static final DateTimeFormatter[] INPUT_DATE_FORMATTERS = {
        createFormatter("uuuu-MM-dd"),
        createFormatter("d/M/uuuu"),
        createFormatter("uuuu/M/d"),
        createFormatter("d-M-uuuu"),
        DateTimeFormatter.ISO_LOCAL_DATE.withResolverStyle(ResolverStyle.STRICT)
    };

    /** Supported input formatters for strings containing only a time. */
    private static final DateTimeFormatter[] INPUT_TIME_FORMATTERS = {
        createFormatter("HHmm"),
        createFormatter("H:mm"),
        createFormatter("h:mma"),
        createFormatter("ha")
    };

    /** Prevents instantiation of this utility class. */
    private TaskDateTimeParser() {
    }

    /**
     * Parses a date or date-time value using every supported format.
     *
     * @param input Raw date or date-time input.
     * @return Parsed task date-time value.
     * @throws OzException If the input is empty, invalid, or nonexistent.
     */
    static TaskDateTime parse(String input) throws OzException {
        if (input == null || input.isBlank()) {
            throw new OzException(EMPTY_DATE_TIME_MESSAGE);
        }

        String normalizedInput = input.trim();
        boolean hasNonExistentDate = false;
        for (DateTimeFormatter formatter : INPUT_DATE_TIME_FORMATTERS) {
            try {
                LocalDateTime parsedDateTime = LocalDateTime.parse(normalizedInput, formatter);
                return TaskDateTime.fromDateTime(parsedDateTime);
            } catch (DateTimeParseException exception) {
                hasNonExistentDate |= reportsInvalidCalendarDate(exception);
            }
        }

        for (DateTimeFormatter formatter : INPUT_DATE_FORMATTERS) {
            try {
                LocalDate parsedDate = LocalDate.parse(normalizedInput, formatter);
                return TaskDateTime.fromDate(parsedDate);
            } catch (DateTimeParseException exception) {
                hasNonExistentDate |= reportsInvalidCalendarDate(exception);
            }
        }

        if (hasNonExistentDate) {
            throw new OzException(NON_EXISTENT_DATE_MESSAGE);
        }
        throw new OzException(INVALID_DATE_TIME_MESSAGE);
    }

    /**
     * Parses a date without accepting a time component.
     *
     * @param input Raw date input.
     * @return Parsed local date.
     * @throws OzException If the input is empty, invalid, or nonexistent.
     */
    static LocalDate parseDate(String input) throws OzException {
        if (input == null || input.isBlank()) {
            throw new OzException(EMPTY_DATE_MESSAGE);
        }

        String normalizedInput = input.trim();
        boolean hasNonExistentDate = false;
        for (DateTimeFormatter formatter : INPUT_DATE_FORMATTERS) {
            try {
                return LocalDate.parse(normalizedInput, formatter);
            } catch (DateTimeParseException exception) {
                hasNonExistentDate |= reportsInvalidCalendarDate(exception);
            }
        }

        if (hasNonExistentDate) {
            throw new OzException(NON_EXISTENT_DATE_MESSAGE);
        }
        throw new OzException(INVALID_DATE_MESSAGE);
    }

    /**
     * Parses a time without accepting a date component.
     *
     * @param input Raw time input.
     * @return Parsed local time.
     * @throws OzException If the input is empty or invalid.
     */
    static LocalTime parseTime(String input) throws OzException {
        if (input == null || input.isBlank()) {
            throw new OzException(EMPTY_TIME_MESSAGE);
        }

        String normalizedInput = input.trim();
        for (DateTimeFormatter formatter : INPUT_TIME_FORMATTERS) {
            try {
                return LocalTime.parse(normalizedInput, formatter);
            } catch (DateTimeParseException ignored) {
                // Trying each supported format is the intended parsing strategy.
            }
        }
        throw new OzException(INVALID_TIME_MESSAGE);
    }

    /**
     * Creates a strict, case-insensitive formatter from a pattern.
     *
     * @param pattern Date or time format pattern.
     * @return Strict, case-insensitive formatter.
     */
    private static DateTimeFormatter createFormatter(String pattern) {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(pattern)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .toFormatter(Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT);
    }

    /**
     * Detects a strict-parser failure caused by an impossible calendar date.
     *
     * @param exception Date-time parsing failure.
     * @return True if the parser identified an invalid calendar date.
     */
    private static boolean reportsInvalidCalendarDate(DateTimeParseException exception) {
        return exception.getMessage() != null
                && exception.getMessage().contains("Invalid date");
    }
}
