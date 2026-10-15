package avon.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.Objects;

/**
 * Parses and formats the date-time values used by timed tasks.
 */
public final class DateTimeParser {
    private static final DateTimeFormatter DATE_TIME_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm")
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATE_DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d uuuu", Locale.ENGLISH);
    private static final DateTimeFormatter DATE_TIME_DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d uuuu, h:mma", Locale.ENGLISH);

    private DateTimeParser() {
    }

    /**
     * Parses an ISO date with an optional 24-hour time.
     *
     * @param value a value in {@code yyyy-MM-dd} or {@code yyyy-MM-dd HHmm} format.
     * @return the parsed value, using midnight when no time is supplied.
     * @throws NullPointerException if the value is null.
     * @throws DateTimeParseException if the value is not a real date and time.
     */
    public static LocalDateTime parse(String value) {
        Objects.requireNonNull(value, "The date-time value cannot be null.");
        try {
            return LocalDateTime.parse(value, DATE_TIME_INPUT_FORMAT);
        } catch (DateTimeParseException exception) {
            return LocalDate.parse(value).atStartOfDay();
        }
    }

    /**
     * Parses a persisted ISO date-time or a legacy date-only value.
     *
     * @param value the stored date-time text.
     * @return the restored date-time.
     * @throws NullPointerException if the value is null.
     * @throws DateTimeParseException if the stored value is invalid.
     */
    public static LocalDateTime parseStoredValue(String value) {
        Objects.requireNonNull(value, "The stored date-time value cannot be null.");
        try {
            return LocalDateTime.parse(value);
        } catch (DateTimeParseException exception) {
            return LocalDate.parse(value).atStartOfDay();
        }
    }

    /**
     * Formats a date-time for display, omitting midnight for date-only inputs.
     *
     * @param dateTime the date-time to format.
     * @return the human-readable date and optional time.
     */
    public static String format(LocalDateTime dateTime) {
        if (dateTime.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return dateTime.format(DATE_DISPLAY_FORMAT);
        }
        return dateTime.format(DATE_TIME_DISPLAY_FORMAT);
    }

}
