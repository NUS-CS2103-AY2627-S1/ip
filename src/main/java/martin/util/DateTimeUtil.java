package martin.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import martin.exception.IllegalCommandException;

/**
 * Utility class for parsing and formatting {@link LocalDateTime} objects in the
 * application.
 */
public class DateTimeUtil {
    public static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HHmm");
    public static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("MMM dd yyyy, HH:mm",
            Locale.ENGLISH);

    /**
     * Parses the given date-time string into a {@link LocalDateTime} object.
     * Accepts the strict user input format {@code yyyy/MM/dd HHmm} or standard
     * ISO-8601 strings.
     *
     * @param input The date-time string to parse.
     * @return The corresponding {@code LocalDateTime} object.
     * @throws IllegalCommandException If the input string cannot be parsed.
     */
    public static LocalDateTime parse(String input) {
        String trimmed = input.trim();
        try {
            return LocalDateTime.parse(trimmed, INPUT_FORMATTER);
        } catch (DateTimeParseException exception) {
            try {
                return LocalDateTime.parse(trimmed);
            } catch (DateTimeParseException ignored) {
                throw new IllegalCommandException(
                        "Please use the date/time format yyyy/MM/dd HHmm (e.g. 2026/08/26 1830).");
            }
        }
    }

    /**
     * Formats the given {@link LocalDateTime} into a user-friendly display string.
     *
     * @param dateTime The {@code LocalDateTime} object to format.
     * @return The formatted date-time string (e.g. "Aug 26 2026, 18:30").
     */
    public static String formatDisplay(LocalDateTime dateTime) {
        return dateTime.format(DISPLAY_FORMATTER);
    }

    /**
     * Formats the given {@link LocalDateTime} into a string representation for file
     * storage.
     *
     * @param dateTime The {@code LocalDateTime} object to format.
     * @return The string formatted for storage.
     */
    public static String formatStorage(LocalDateTime dateTime) {
        return dateTime.format(INPUT_FORMATTER);
    }
}
