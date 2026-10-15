package zaru.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

import zaru.exception.ZaruException;

/**
 * Handles parsing and formatting date-time values used in task commands.
 */
public final class DateTimeParser {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern(
            "MMM dd yyyy, h:mma", Locale.ENGLISH);

    /**
     * Parses user-entered date-time text.
     *
     * @param text Date-time text in {@code yyyy-MM-dd HHmm} format, or date-only text in {@code yyyy-MM-dd} format.
     * @return Parsed date and time. Date-only input is interpreted as midnight.
     * @throws ZaruException If the text is missing or does not match the accepted formats.
     */
    public static LocalDateTime parse(String text) throws ZaruException {
        if (text == null || text.isBlank()) {
            throw new ZaruException("Please provide a date and time.");
        }

        try {
            return LocalDateTime.parse(text.strip(), DATE_TIME_FORMAT);
        } catch (DateTimeParseException e) {
            return parseDateOnly(text.strip());
        }
    }

    /**
     * Formats a date-time value for display to the user.
     *
     * @param dateTime Date-time value to format.
     * @return User-friendly date-time text.
     */
    public static String format(LocalDateTime dateTime) {
        return dateTime.format(DISPLAY_FORMAT);
    }

    /**
     * Formats a date-time value for storage.
     *
     * @param dateTime Date-time value to format.
     * @return Stable save-file date-time text.
     */
    public static String formatForStorage(LocalDateTime dateTime) {
        return dateTime.format(DATE_TIME_FORMAT);
    }

    /**
     * Parses date-only input and treats it as the start of that day.
     *
     * @param text Date text in {@code yyyy-MM-dd} format.
     * @return Parsed date at midnight.
     * @throws ZaruException If the text is not in an accepted date-time format.
     */
    private static LocalDateTime parseDateOnly(String text) throws ZaruException {
        try {
            LocalDate date = LocalDate.parse(text, DATE_FORMAT);
            return LocalDateTime.of(date, LocalTime.MIDNIGHT);
        } catch (DateTimeParseException e) {
            throw new ZaruException("Please enter dates in yyyy-MM-dd or yyyy-MM-dd HHmm format.");
        }
    }
}
