package downtowngurl.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.util.Locale;

import downtowngurl.exception.DowntownGurlException;

/**
 * Parses and formats date-time values used by deadline and event tasks.
 */
public class TaskDateTime {
    private static final DateTimeFormatter INPUT_FORMAT = new DateTimeFormatterBuilder()
            .appendPattern("d/M/uuuu")
            .optionalStart()
            .appendLiteral(' ')
            .appendValue(ChronoField.HOUR_OF_DAY, 2)
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .optionalEnd()
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .toFormatter(Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter STORAGE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, EEEE HH:mm", Locale.ENGLISH);

    /**
     * Parses user input such as "2/12/2019 1800" into a LocalDateTime.
     *
     * @param input Date-time text entered by the user.
     * @return Parsed date-time.
     * @throws DowntownGurlException If the text is not in an accepted date-time format.
     */
    public static LocalDateTime parse(String input) throws DowntownGurlException {
        try {
            return LocalDateTime.parse(input.trim(), INPUT_FORMAT);
        } catch (DateTimeParseException e) {
            throw new DowntownGurlException("Use dates like 2/12/2019 1800, bestie.");
        }
    }

    /**
     * Parses date-time text from storage.
     *
     * @param input Stored date-time text.
     * @return Parsed date-time.
     * @throws DowntownGurlException If the stored text is not in an accepted format.
     */
    public static LocalDateTime parseFromStorage(String input) throws DowntownGurlException {
        try {
            return LocalDateTime.parse(input.trim(), STORAGE_FORMAT);
        } catch (DateTimeParseException e) {
            return parse(input);
        }
    }

    /**
     * Formats a date-time for saving to disk.
     *
     * @param dateTime Date-time to save.
     * @return Stable storage representation.
     */
    public static String formatForStorage(LocalDateTime dateTime) {
        return dateTime.format(STORAGE_FORMAT);
    }

    /**
     * Formats a date-time for display to the user.
     *
     * @param dateTime Date-time to display.
     * @return Display representation in dd MMM yyyy, day time form.
     */
    public static String formatForDisplay(LocalDateTime dateTime) {
        return dateTime.format(DISPLAY_FORMAT);
    }
}
