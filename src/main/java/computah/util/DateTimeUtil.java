package computah.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import computah.exception.ComputahException;

/**
 * Converts user-entered date/time strings into typed values and display text.
 */
public class DateTimeUtil {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy h:mm a", Locale.ENGLISH);
    private static final DateTimeFormatter FILE_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
    private static final DateTimeFormatter SLASH_DATE_FORMAT =
            DateTimeFormatter.ofPattern("d/M/yyyy");
    private static final DateTimeFormatter SLASH_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("d/M/yyyy HHmm");

    /**
     * Prevents instantiation of this utility class.
     */
    private DateTimeUtil() {
    }

    /**
     * Parses supported user date/time formats into a LocalDateTime.
     *
     * @param text date/time text to parse.
     * @return parsed date/time, using midnight when the input contains only a date.
     * @throws ComputahException if the text does not match any supported format.
     */
    public static LocalDateTime parse(String text) throws ComputahException {
        String trimmedText = text.trim();
        try {
            return LocalDate.parse(trimmedText).atStartOfDay();
        } catch (DateTimeParseException e) {
            // Try the next supported format.
        }
        try {
            return LocalDateTime.parse(trimmedText, FILE_DATE_TIME_FORMAT);
        } catch (DateTimeParseException e) {
            // Try the next supported format.
        }
        try {
            return LocalDate.parse(trimmedText, SLASH_DATE_FORMAT).atStartOfDay();
        } catch (DateTimeParseException e) {
            // Try the next supported format.
        }
        try {
            return LocalDateTime.parse(trimmedText, SLASH_DATE_TIME_FORMAT);
        } catch (DateTimeParseException e) {
            throw new ComputahException("Date/time must be in yyyy-MM-dd, yyyy-MM-dd HHmm, "
                    + "d/M/yyyy, or d/M/yyyy HHmm format.");
        }
    }

    /**
     * Formats a date/time for display to the user.
     *
     * @param dateTime date/time to format.
     * @return date-only text for midnight values, or date-time text otherwise.
     */
    public static String formatForDisplay(LocalDateTime dateTime) {
        if (dateTime.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return dateTime.format(DISPLAY_DATE_FORMAT);
        }
        return dateTime.format(DISPLAY_DATE_TIME_FORMAT);
    }

    /**
     * Formats a date/time for storage in the save file.
     *
     * @param dateTime date/time to format.
     * @return ISO date text for midnight values, or ISO date plus 24-hour time otherwise.
     */
    public static String formatForFile(LocalDateTime dateTime) {
        if (dateTime.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return dateTime.toLocalDate().toString();
        }
        return dateTime.format(FILE_DATE_TIME_FORMAT);
    }
}
