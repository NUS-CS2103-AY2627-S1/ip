package harold.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Parses and formats dates used by tasks.
 */
public final class TaskDate {
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    /**
     * Prevents instantiation of this date utility class.
     */
    private TaskDate() {
    }

    /**
     * Parses a date in the ISO yyyy-MM-dd format.
     *
     * @param dateText Date text in {@code yyyy-MM-dd} format.
     * @return Parsed date.
     * @throws java.time.format.DateTimeParseException If the text is not a valid ISO date.
     */
    public static LocalDate parse(String dateText) {
        return LocalDate.parse(dateText, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /**
     * Formats a date for display to the user.
     *
     * @param date Date to format.
     * @return Date formatted with an abbreviated English month.
     */
    public static String format(LocalDate date) {
        return date.format(DISPLAY_FORMATTER);
    }
}
