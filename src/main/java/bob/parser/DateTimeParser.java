package bob.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

/**
 * Parses and formats the date and time values used by Bob's tasks.
 */
public class DateTimeParser {
    private static final DateTimeFormatter INPUT_DATE_FORMAT = new DateTimeFormatterBuilder()
            .appendPattern("d/M/uuuu")
            .toFormatter(Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter INPUT_DATE_TIME_FORMAT = new DateTimeFormatterBuilder()
            .appendPattern("d/M/uuuu HHmm")
            .toFormatter(Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter OUTPUT_DATE_FORMAT = DateTimeFormatter.ofPattern(
            "MMM dd uuuu", Locale.ENGLISH);
    private static final DateTimeFormatter OUTPUT_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern(
            "MMM dd uuuu HHmm'hrs'", Locale.ENGLISH);
    private static final DateTimeFormatter STORAGE_DATE_FORMAT = DateTimeFormatter.ofPattern(
            "dd/MM/uuuu", Locale.ENGLISH);
    private static final DateTimeFormatter STORAGE_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern(
            "dd/MM/uuuu HHmm", Locale.ENGLISH);

    /** Prevents instantiation of this utility class. */
    private DateTimeParser() {
    }

    /**
     * Parses a date or date-time using Bob's accepted input formats.
     *
     * @param value Date or date-time text supplied by the user.
     * @return A LocalDate for a date, or LocalDateTime for a date with time.
     * @throws DateTimeParseException If the value is not a valid supported date or date-time.
     */
    public static TemporalAccessor parse(String value) {
        try {
            return LocalDate.parse(value, INPUT_DATE_FORMAT);
        } catch (DateTimeParseException dateException) {
            return LocalDateTime.parse(value, INPUT_DATE_TIME_FORMAT);
        }
    }

    /**
     * Formats a date or date-time for display to the user.
     *
     * @param value Date or date-time to format.
     * @return Formatted date or date-time.
     */
    public static String formatForDisplay(TemporalAccessor value) {
        if (value instanceof LocalDateTime dateTime) {
            return dateTime.format(OUTPUT_DATE_TIME_FORMAT);
        }
        return ((LocalDate) value).format(OUTPUT_DATE_FORMAT);
    }

    /**
     * Formats a date or date-time for storage using an accepted input format.
     *
     * @param value Date or date-time to format.
     * @return Storage representation of the value.
     */
    public static String formatForStorage(TemporalAccessor value) {
        if (value instanceof LocalDateTime dateTime) {
            return dateTime.format(STORAGE_DATE_TIME_FORMAT);
        }
        return ((LocalDate) value).format(STORAGE_DATE_FORMAT);
    }
}
