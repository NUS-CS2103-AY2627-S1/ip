package yokohama.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

/**
 * Converts date-time values between strings and {@link LocalDateTime} objects.
 */
public class DateTimeHandler {

    /** Formatter for the application's date-time input and display format. */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("M/d/uuuu HHmm")
            .withResolverStyle(ResolverStyle.STRICT);
    /** Formatter for a date without a time. */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    /**
     * Converts a date-time string in the application's standard format to a {@code LocalDateTime}.
     *
     * @param dateTime Date-time string in {@code M/d/yyyy HHmm} format.
     * @return Parsed date-time value.
     */
    public static LocalDateTime convertToLocalDateTime(String dateTime) {
        return LocalDateTime.parse(dateTime, DATE_TIME_FORMATTER);
    }

    /**
     * Converts a deadline value with an optional time. Date-only deadlines use
     * the end of the day so they remain useful for date-based scheduling.
     *
     * @param deadline Deadline in {@code M/d/yyyy} or {@code M/d/yyyy HHmm} format.
     * @return Parsed deadline date and time.
     */
    public static LocalDateTime convertToDeadlineDateTime(String deadline) {
        if (deadline.matches("\\d{1,2}/\\d{1,2}/\\d{4}")) {
            return convertToLocalDate(deadline).atTime(LocalTime.of(23, 59));
        }
        return convertToLocalDateTime(deadline);
    }

    /**
     * Converts a date string in the application's standard schedule format to a {@code LocalDate}.
     *
     * @param date Date string in {@code M/d/yyyy} format.
     * @return Parsed date value.
     */
    public static LocalDate convertToLocalDate(String date) {
        return LocalDate.parse(date, DATE_FORMATTER);
    }

    /**
     * Returns a date-time value in the application's standard readable format.
     *
     * @param dateTime Date-time value to format.
     * @return Date-time string in {@code M/d/yyyy HHmm} format.
     */
    public static String formatToReadable(LocalDateTime dateTime) {
        return dateTime.format(DATE_TIME_FORMATTER);
    }
}
