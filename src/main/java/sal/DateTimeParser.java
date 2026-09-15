package sal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Parses user-entered date/time strings and formats stored dates for display.
 */
public class DateTimeParser {
    private static final DateTimeFormatter[] DATE_TIME_FORMATTERS = {
        DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"),
        DateTimeFormatter.ofPattern("d/M/yyyy HHmm"),
    };

    private static final DateTimeFormatter[] DATE_FORMATTERS = {
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        DateTimeFormatter.ofPattern("d/M/yyyy"),
    };

    private static final DateTimeFormatter STORAGE_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    private static final DateTimeFormatter STORAGE_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /** English locale keeps month names and AM/PM markers the same on every OS. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a", Locale.ENGLISH);

    /**
     * Parses a date/time string into a {@link TaskDateTime}.
     * Date-only inputs are marked as having no time; date-time inputs keep an explicit time
     * even if that time is midnight.
     *
     * @param input Date/time text such as {@code 2019-10-15} or {@code 2/12/2019 1800}.
     * @return Parsed date or date-time.
     * @throws DateTimeParseException If the input does not match a supported format.
     */
    public static TaskDateTime parse(String input) {
        String trimmed = input.trim();

        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                return new TaskDateTime(LocalDateTime.parse(trimmed, formatter));
            } catch (DateTimeParseException e) {
                // Try the next format.
            }
        }

        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return new TaskDateTime(LocalDate.parse(trimmed, formatter));
            } catch (DateTimeParseException e) {
                // Try the next format.
            }
        }

        throw new DateTimeParseException("Unable to parse date/time: " + input, input, 0);
    }

    /**
     * Formats a date/time for saving to disk.
     * Date-only values use {@code yyyy-MM-dd}; date-times use {@code yyyy-MM-dd HHmm}.
     *
     * @param dateTime Date or date-time to format.
     * @return Text suitable for the data file.
     */
    public static String formatForStorage(TaskDateTime dateTime) {
        if (dateTime.hasTime()) {
            return dateTime.getValue().format(STORAGE_DATE_TIME_FORMATTER);
        }
        return dateTime.getValue().toLocalDate().format(STORAGE_DATE_FORMATTER);
    }

    /**
     * Formats a date/time for display to the user.
     *
     * @param dateTime Date or date-time to format.
     * @return Text such as {@code Oct 15 2019} or {@code Dec 02 2019, 6:00 PM}.
     */
    public static String formatForDisplay(TaskDateTime dateTime) {
        if (dateTime.hasTime()) {
            return dateTime.getValue().format(DISPLAY_DATE_TIME_FORMATTER);
        }
        return dateTime.getValue().format(DISPLAY_DATE_FORMATTER);
    }
}
