package luna.task;

import java.time.LocalDate; // Represents a date without the time
import java.time.format.DateTimeFormatter; // Formatter for printing and parsing date-time objects
import java.time.format.DateTimeParseException; // An exception thrown when an error occurs during parsing

import luna.exception.LunaException;

/**
 * Parses and formats dates used by time-related tasks.
 */
public final class TaskDateFormatter {
    private static final DateTimeFormatter DATE_STORAGE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE; // yyyy-MM-dd
    private static final DateTimeFormatter DATE_DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");

    private TaskDateFormatter() {
    }

    /**
     * Parses a date in the task storage format.
     *
     * @param dateText Date in {@code yyyy-MM-dd} format.
     * @return Parsed date.
     * @throws LunaException If the date is not in the expected format.
     */
    public static LocalDate parse(String dateText) throws LunaException {
        try {
            return LocalDate.parse(dateText, DATE_STORAGE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new LunaException("Invalid date format. Use yyyy-MM-dd (e.g., 2019-10-15)");
        }
    }

    /**
     * Formats a date for display to the user.
     *
     * @param date Date to format.
     * @return Date in display format.
     */
    public static String formatForDisplay(LocalDate date) {
        return date.format(DATE_DISPLAY_FORMAT);
    }

    /**
     * Formats a date for persistent storage.
     *
     * @param date Date to format.
     * @return Date in storage format.
     */
    public static String formatForStorage(LocalDate date) {
        return date.format(DATE_STORAGE_FORMAT);
    }
}
