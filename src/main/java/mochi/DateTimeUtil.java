package mochi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * Parses date-time input and formats date-times for display and storage.
 */
public final class DateTimeUtil {
    /** Description of the date-time format accepted in user commands. */
    public static final String INPUT_FORMAT_DESCRIPTION = "yyyy-MM-dd HHmm";

    private static final DateTimeFormatter INPUT_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("uuuu-MM-dd HHmm")
            .toFormatter(Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd uuuu, h:mm a", Locale.ENGLISH);

    private DateTimeUtil() {
    }

    /**
     * Parses a date-time in the command and storage format.
     *
     * @param value Date-time text using {@code yyyy-MM-dd HHmm}.
     * @return Parsed date-time.
     */
    public static LocalDateTime parse(String value) {
        return LocalDateTime.parse(value, INPUT_FORMATTER);
    }

    /**
     * Formats a date-time for the human-readable task display.
     *
     * @param dateTime Date-time to format.
     * @return Date-time such as {@code Dec 02 2019, 6:00 PM}.
     */
    public static String formatForDisplay(LocalDateTime dateTime) {
        return dateTime.format(DISPLAY_FORMATTER);
    }

    /**
     * Formats a date-time for reliable parsing from the data file.
     *
     * @param dateTime Date-time to format.
     * @return Date-time using {@code yyyy-MM-dd HHmm}.
     */
    public static String formatForStorage(LocalDateTime dateTime) {
        return dateTime.format(INPUT_FORMATTER);
    }
}
