package friday.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import friday.FridayException;

/**
 * Parses and formats task date-time values consistently.
 */
public final class TaskDateTime {
    private static final DateTimeFormatter FILE_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("d/M/uuuu HHmm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy h:mma");
    private static final String INVALID_DATE_TIME_MESSAGE =
            "Apologies, please use the date format yyyy-MM-dd HHmm or d/M/yyyy HHmm";

    private TaskDateTime() {
        // Utility class.
    }

    /**
     * Parses a date-time from either the save-file format or the user input format.
     *
     * @param dateTime date and time text to parse
     * @return parsed date and time
     * @throws FridayException if the date and time cannot be parsed
     */
    public static LocalDateTime parse(String dateTime) throws FridayException {
        try {
            return LocalDateTime.parse(dateTime, FILE_FORMAT);
        } catch (DateTimeParseException fileFormatException) {
            try {
                return LocalDateTime.parse(dateTime, INPUT_FORMAT);
            } catch (DateTimeParseException inputFormatException) {
                throw new FridayException(INVALID_DATE_TIME_MESSAGE);
            }
        }
    }

    /**
     * Formats a date-time for storage.
     *
     * @param dateTime date and time to format
     * @return save-file representation
     */
    public static String formatForFile(LocalDateTime dateTime) {
        return dateTime.format(FILE_FORMAT);
    }

    /**
     * Formats a date-time for display.
     *
     * @param dateTime date and time to format
     * @return user-facing representation
     */
    public static String formatForDisplay(LocalDateTime dateTime) {
        return dateTime.format(DISPLAY_FORMAT);
    }
}
