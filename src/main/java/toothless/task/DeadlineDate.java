package toothless.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Parses and formats dates used by deadline tasks.
 */
public final class DeadlineDate {
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    private DeadlineDate() {
        // This utility class should not be instantiated.
    }

    /**
     * Parses a deadline date written in the ISO {@code yyyy-MM-dd} format.
     *
     * @param value date text to parse.
     * @return parsed date
     * @throws java.time.format.DateTimeParseException if the date is invalid
     */
    public static LocalDate parse(String value) {
        return LocalDate.parse(value);
    }

    /**
     * Formats a deadline date for friendly task-list display.
     *
     * @param date date to format.
     * @return date in {@code MMM d yyyy} format
     */
    public static String format(LocalDate date) {
        return date.format(DISPLAY_FORMATTER);
    }
}
