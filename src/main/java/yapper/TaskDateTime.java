package yapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;

/**
 * Parses task dates from supported user formats and formats them for display and storage.
 */
public final class TaskDateTime {
    /** Supported date-time parsers, tried in order. */
    private static final List<DateTimeFormatter> DATE_TIME_INPUT_FORMATS = List.of(
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm").withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm").withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    /** Supported date-only parsers, tried after date-time parsing fails. */
    private static final List<DateTimeFormatter> DATE_INPUT_FORMATS = List.of(
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT));
    /** Display format used for values at midnight. */
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("MMM d uuuu");
    /** Display format used for values with a non-midnight time. */
    private static final DateTimeFormatter DISPLAY_DATE_TIME =
            DateTimeFormatter.ofPattern("MMM d uuuu, h:mma");

    /**
     * Prevents instantiation of this utility class.
     */
    private TaskDateTime() {
    }

    /**
     * Parses ISO local date-time text or a date in {@code yyyy-MM-dd} or {@code d/M/yyyy} form.
     * Either date form may be followed by a space and a four-digit 24-hour time.
     * Date-only values are represented as midnight on that date.
     *
     * @param value Date text supplied by the user or storage file.
     * @return Parsed date and time.
     * @throws IllegalArgumentException If the value does not match a supported format.
     */
    public static LocalDateTime parse(String value) {
        for (DateTimeFormatter formatter : DATE_TIME_INPUT_FORMATS) {
            try {
                return LocalDateTime.parse(value, formatter);
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }
        for (DateTimeFormatter formatter : DATE_INPUT_FORMATS) {
            try {
                return LocalDate.parse(value, formatter).atStartOfDay();
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }
        throw new IllegalArgumentException("invalid date or time");
    }

    /**
     * Parses a calendar date without a time.
     *
     * @param value Date in {@code yyyy-MM-dd} or {@code d/M/yyyy} format.
     * @return Parsed calendar date.
     * @throws IllegalArgumentException If the value is not a supported date.
     */
    public static LocalDate parseDate(String value) {
        for (DateTimeFormatter formatter : DATE_INPUT_FORMATS) {
            try {
                return LocalDate.parse(value, formatter);
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }
        throw new IllegalArgumentException("invalid date");
    }

    /**
     * Returns a friendly representation suitable for chatbot output.
     *
     * @param dateTime Date and time to format.
     * @return Date in {@code MMM d uuuu} form, with {@code h:mma} appended unless the time is midnight.
     */
    public static String formatForDisplay(LocalDateTime dateTime) {
        if (dateTime.toLocalTime().equals(java.time.LocalTime.MIDNIGHT)) {
            return dateTime.format(DISPLAY_DATE);
        }
        return dateTime.format(DISPLAY_DATE_TIME);
    }

    /**
     * Returns the stable ISO representation used in the save file.
     *
     * @param dateTime Date and time to format.
     * @return ISO local date-time text, without a time zone.
     */
    public static String formatForStorage(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
