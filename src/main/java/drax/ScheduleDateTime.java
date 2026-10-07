package drax;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Converts the date and time text used by commands into values that can be compared and formatted reliably.
 */
public final class ScheduleDateTime {
    private static final DateTimeFormatter COMMAND_DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("d/M/uuuu HHmm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd uuuu");
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd uuuu h:mm a");
    private static final String ACCEPTED_FORMATS = "yyyy-MM-dd, yyyy-MM-ddTHH:mm, or d/M/yyyy HHmm";
    private static final String INVALID_DATE_TIME_MESSAGE = "Please use a valid date and time: "
            + ACCEPTED_FORMATS + "!";

    private ScheduleDateTime() {
    }

    /**
     * Parses a supported date or date-time representation.
     * Accepts ISO local dates, ISO local date-times, and command date-times
     * in the {@code d/M/uuuu HHmm} format.
     * A date without a time is represented as midnight.
     *
     * @param text date or date-time text from command input or storage.
     * @return the parsed local date and time
     * @throws IllegalArgumentException if {@code text} has an invalid or unsupported format
     */
    public static LocalDateTime parse(String text) {
        try {
            if (text == null || text.isBlank()) {
                throw new IllegalArgumentException(INVALID_DATE_TIME_MESSAGE);
            }
            if (text.contains(" ")) {
                return LocalDateTime.parse(text, COMMAND_DATE_TIME_FORMATTER);
            }
            if (text.contains("T")) {
                return LocalDateTime.parse(text);
            }
            return LocalDate.parse(text).atStartOfDay();
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException(INVALID_DATE_TIME_MESSAGE, exception);
        }
    }

    /**
     * Formats a schedule date and time for user-facing display. Midnight values are shown as dates only.
     *
     * @param dateTime date and time to display.
     * @return a readable date or date and time
     */
    public static String formatForDisplay(LocalDateTime dateTime) {
        boolean isMidnight = dateTime.toLocalTime().equals(LocalTime.MIDNIGHT);
        if (isMidnight) {
            return dateTime.format(DISPLAY_DATE_FORMATTER);
        }
        return dateTime.format(DISPLAY_DATE_TIME_FORMATTER);
    }
}
