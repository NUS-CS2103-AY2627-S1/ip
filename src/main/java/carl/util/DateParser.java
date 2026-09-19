package carl.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;


/**
 * Utility class for parsing and formatting dates.
 */
public class DateParser {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("uuuu-MM-dd HHmm")
            .toFormatter(Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATE_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("uuuu-MM-dd")
            .toFormatter(Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd uuuu, h:mm a", Locale.ENGLISH);

    private DateParser() {

    }

    /**
     * Parses a string into a LocalDateTime object.
     *
     * @param strDate The date string to parse, expected in "yyyy-MM-dd HHmm" format.
     * @return The parsed LocalDateTime object.
     * @throws DateTimeParseException If the string cannot be parsed into a valid date and time.
     */
    public static LocalDateTime parseDateTime(String strDate) throws DateTimeParseException {
        if (strDate == null || !strDate.matches("\\d{4}-\\d{2}-\\d{2} \\d{4}")) {
            throw new DateTimeParseException("Invalid date and time format", String.valueOf(strDate), 0);
        }
        return LocalDateTime.parse(strDate, DATE_TIME_FORMATTER);
    }

    /**
     * Parses a string into a LocalDate object without time.
     *
     * @param strDate The date string to parse, expected in "yyyy-MM-dd" format.
     * @return The parsed LocalDate object.
     * @throws DateTimeParseException If the string cannot be parsed into a valid date.
     */
    public static LocalDate parseDate(String strDate) throws DateTimeParseException {
        if (strDate == null || !strDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new DateTimeParseException("Invalid date format", String.valueOf(strDate), 0);
        }
        return LocalDate.parse(strDate, DATE_FORMATTER);
    }

    /**
     * Returns an error message for invalid date and time format.
     *
     * @return The error message string.
     */
    public static String getDateTimeErrorMessage() {
        return "Invalid date inputted! Correct format: \"yyyy-MM-dd HHmm\", e.g., \"2026-12-24 1800\"";
    }

    /**
     * Returns an error message for invalid date format (without time).
     *
     * @return The error message string.
     */
    public static String getDateErrorMessage() {
        return "Invalid date inputted! Correct format: \"yyyy-MM-dd\", e.g., \"2026-12-24\"";
    }

    /**
     * Provides a formatter for displaying dates to the user.
     *
     * @return A DateTimeFormatter with the pattern "MMM dd yyyy, h:mm a".
     */
    public static DateTimeFormatter getDisplayFormatter() {
        return DISPLAY_FORMATTER;
    }

    /**
     * Provides a formatter for saving dates to storage.
     *
     * @return A DateTimeFormatter with the pattern "yyyy-MM-dd HHmm".
     */
    public static DateTimeFormatter getStorageFormatter() {
        return DATE_TIME_FORMATTER;
    }
}
