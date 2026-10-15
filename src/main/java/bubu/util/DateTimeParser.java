package bubu.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import bubu.exception.InvalidDateTimeException;

/** Provides the application's shared date-time parsing and formatting rules. */
public final class DateTimeParser {
    private static final String DATE_TIME_PATTERN = "uuuu-MM-dd HHmm";
    private static final String INVALID_STORED_DATE_TIME_MESSAGE = "Invalid stored date-time: ";
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)
                    .withResolverStyle(ResolverStyle.STRICT);

    private DateTimeParser() {
        // Utility class; do not instantiate.
    }

    /**
     * Parses a date-time or date-only value, using {@code defaultTime} for a date-only value.
     *
     * @param input date-time in yyyy-MM-dd HHmm format, or date in yyyy-MM-dd format.
     * @param defaultTime time to use when the input contains only a date.
     * @return parsed date and time.
     * @throws InvalidDateTimeException if the input matches neither format.
     */
    public static LocalDateTime parse(String input, LocalTime defaultTime)
            throws InvalidDateTimeException {
        try {
            return LocalDateTime.parse(input, DATE_TIME_FORMAT);
        } catch (DateTimeParseException notFullDateTime) {
            try {
                return LocalDate.parse(input).atTime(defaultTime);
            } catch (DateTimeParseException invalidDate) {
                throw new InvalidDateTimeException();
            }
        }
    }

    /** Parses a value from storage and reports malformed data as an unchecked error. */
    public static LocalDateTime parseStored(String input, LocalTime defaultTime) {
        try {
            return parse(input, defaultTime);
        } catch (InvalidDateTimeException exception) {
            throw new IllegalArgumentException(INVALID_STORED_DATE_TIME_MESSAGE + input, exception);
        }
    }

    /** Formats a date-time for storage. */
    public static String format(LocalDateTime dateTime) {
        return dateTime.format(DATE_TIME_FORMAT);
    }
}
