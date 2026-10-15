package probe.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Parses and formats the date/time representations used by the application.
 */
public final class DateTimeParser {
    private static final DateTimeFormatter DAY_MONTH_YEAR_TIME =
            DateTimeFormatter.ofPattern("d/M/yyyy HHmm");
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma");

    /**
     * Prevents instantiation of this utility class.
     */
    private DateTimeParser() {
    }

    /**
     * Returns the date/time parsed from a supported date or date/time string.
     *
     * @param value Date or date/time string to parse.
     * @return Parsed date/time.
     * @throws IllegalArgumentException If the value has an invalid format.
     */
    public static LocalDateTime parse(String value) {
        String input = value.trim();
        try {
            if (input.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return LocalDate.parse(input).atStartOfDay();
            }
            return LocalDateTime.parse(input, DAY_MONTH_YEAR_TIME);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Invalid date/time. Use yyyy-MM-dd or d/M/yyyy HHmm.", e);
        }
    }

    /**
     * Returns a date/time formatted for display to the user.
     *
     * @param value Date/time to format.
     * @return Display-formatted date/time.
     */
    public static String formatForDisplay(LocalDateTime value) {
        return value.format(DISPLAY_FORMAT)
                .replace("AM", "am")
                .replace("PM", "pm");
    }

    /**
     * Returns a date/time formatted in ISO format for persistent storage.
     *
     * @param value Date/time to format.
     * @return Storage-formatted date/time.
     */
    public static String formatForStorage(LocalDateTime value) {
        return value.toString();
    }
}
