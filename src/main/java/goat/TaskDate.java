package goat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Parses and formats dates used by tasks.
 */
public final class TaskDate {
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private TaskDate() {
    }

    /**
     * Parses a date in the application's {@code yyyy-MM-dd} input format.
     *
     * @param value date text to parse
     * @param fieldName name used to identify the invalid field
     * @return parsed date
     * @throws GoatException if the value is not a valid ISO calendar date
     */
    public static LocalDate parse(String value, String fieldName) throws GoatException {
        try {
            return LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException exception) {
            throw new GoatException(fieldName + " must be a valid date in yyyy-MM-dd format.");
        }
    }

    /**
     * Formats a date for display to the user.
     *
     * @param date date to format
     * @return date in {@code MMM dd yyyy} format
     */
    public static String format(LocalDate date) {
        return date.format(DISPLAY_FORMATTER);
    }

    /**
     * Ensures that a date range progresses forwards in time.
     *
     * @param start first date in the range
     * @param end last date in the range
     * @param subject name used in the error message
     * @throws GoatException if the start is the same as or later than the end
     */
    public static void requireStartBeforeEnd(LocalDate start, LocalDate end, String subject)
            throws GoatException {
        if (!start.isBefore(end)) {
            throw new GoatException(subject + " must start before it ends.");
        }
    }
}
