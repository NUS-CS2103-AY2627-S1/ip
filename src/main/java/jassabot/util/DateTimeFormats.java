package jassabot.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;

/**
 * Parses user-entered dates and formats task dates for display and storage.
 */
public final class DateTimeFormats {
    private static final List<DateTimeFormatter> USER_DATE_TIME_FORMATS = List.of(
            strictFormatter("uuuu-MM-dd HHmm"),
            strictFormatter("d/M/uuuu HHmm"));
    private static final List<DateTimeFormatter> USER_DATE_FORMATS = List.of(
            strictFormatter("uuuu-MM-dd"),
            strictFormatter("d/M/uuuu"));
    private static final DateTimeFormatter DATE_DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d uuuu", Locale.ENGLISH);
    private static final DateTimeFormatter DATE_TIME_DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d uuuu, h:mm a", Locale.ENGLISH);
    private static final DateTimeFormatter STORAGE_FORMAT = strictFormatter("uuuu-MM-dd'T'HH:mm");

    private DateTimeFormats() {
    }

    /**
     * Parses a date with an optional 24-hour time. A date without a time starts at midnight.
     *
     * @param value Date entered by the user.
     * @return Parsed date and time.
     * @throws DateTimeParseException If the value is not a supported or valid date.
     */
    public static LocalDateTime parseUserDateTime(String value) {
        for (DateTimeFormatter formatter : USER_DATE_TIME_FORMATS) {
            try {
                return LocalDateTime.parse(value, formatter);
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }
        for (DateTimeFormatter formatter : USER_DATE_FORMATS) {
            try {
                return LocalDate.parse(value, formatter).atStartOfDay();
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }
        throw new DateTimeParseException("Unsupported date and time format", value, 0);
    }

    /**
     * Formats a date and time in a friendly form for chatbot responses.
     *
     * @param dateTime Date and time to display.
     * @return Value such as {@code Dec 2 2019, 6:00 PM}.
     */
    public static String formatForDisplay(LocalDateTime dateTime) {
        if (dateTime.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return dateTime.format(DATE_DISPLAY_FORMAT);
        }
        return dateTime.format(DATE_TIME_DISPLAY_FORMAT);
    }

    /**
     * Formats a date and time in a locale-independent form for persistent storage.
     *
     * @param dateTime Date and time to store.
     * @return Value such as {@code 2019-12-02T18:00}.
     */
    public static String formatForStorage(LocalDateTime dateTime) {
        return dateTime.format(STORAGE_FORMAT);
    }

    /**
     * Parses the stable date-time representation written to persistent storage.
     *
     * @param value Stored date and time.
     * @return Parsed date and time.
     * @throws DateTimeParseException If the stored value is malformed.
     */
    public static LocalDateTime parseStorageDateTime(String value) {
        return LocalDateTime.parse(value, STORAGE_FORMAT);
    }

    /**
     * Creates a locale-independent formatter that rejects invalid calendar dates.
     *
     * @param pattern Pattern used to parse and format dates.
     * @return Strict date-time formatter for the pattern.
     */
    private static DateTimeFormatter strictFormatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT);
    }
}
