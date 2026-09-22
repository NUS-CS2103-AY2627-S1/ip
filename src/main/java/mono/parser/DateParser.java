package mono.parser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Provides shared date parsing and display formatting for task types.
 */
public final class DateParser {
    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    /** Prevents instantiation of this utility class. */
    private DateParser() {
        // Utility class; do not instantiate.
    }

    /**
     * Parses a date in ISO-8601 format.
     *
     * @param dateText date in {@code yyyy-MM-dd} format
     * @return parsed date
     */
    public static LocalDate parseDate(String dateText) {
        return LocalDate.parse(dateText, INPUT_FORMAT);
    }

    /**
     * Formats a date for display in task responses.
     *
     * @param date date to format
     * @return date in {@code MMM dd yyyy} format
     */
    public static String formatDate(LocalDate date) {
        return date.format(DISPLAY_FORMAT);
    }
}
