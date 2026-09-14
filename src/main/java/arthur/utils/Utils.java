package arthur.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

import arthur.exceptions.InvalidDateTimeException;

/**
 * Provides date and time parsing and formatting utilities.
 */
public class Utils {
    private static final DateTimeFormatter DATETIME_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATETIME_OUTPUT_FORMAT = DateTimeFormatter.ofPattern("MMM dd uuuu @ HH:mm");
    private static final DateTimeFormatter DATE_INPUT_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    /**
     * Parses a string into a datetime object.
     *
     * @param dateTime the datetime string in {@code yyyy-MM-dd HHmm} format.
     * @return the parsed datetime object.
     * @throws InvalidDateTimeException if the input is not a valid datetime.
     */
    public static LocalDateTime parseDateTime(String dateTime) {
        try {
            return LocalDateTime.parse(dateTime, DATETIME_INPUT_FORMAT);
        } catch (DateTimeParseException e) {
            throw new InvalidDateTimeException(dateTime);
        }
    }

    /**
     * Formats a datetime object into a string.
     *
     * @param dateTime the datetime object.
     * @return the formatted date.
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATETIME_OUTPUT_FORMAT);
    }

    // AI-assisted: Added ISO date parsing for the filter command.
    /**
     * Parses an ISO-8601 calendar date.
     *
     * @param date the date in {@code yyyy-MM-dd} format.
     * @return the parsed date.
     * @throws InvalidDateTimeException if the input is not a valid date.
     */
    public static LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date, DATE_INPUT_FORMAT);
        } catch (DateTimeParseException e) {
            throw new InvalidDateTimeException(date);
        }
    }
}
