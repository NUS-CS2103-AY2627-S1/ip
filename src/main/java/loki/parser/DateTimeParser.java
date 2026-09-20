package loki.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;

/**
 * Parses and formats the date/time values used by task objects and storage.
 */
public final class DateTimeParser {
    private static final DateTimeFormatter DAY_MONTH_YEAR_TIME =
            new DateTimeFormatterBuilder()
                    .appendPattern("d/M/uuuu HHmm")
                    .toFormatter(Locale.ROOT)
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter ISO_DATE_TIME =
            new DateTimeFormatterBuilder()
                    .appendPattern("uuuu-MM-dd['T'][' ']HHmm")
                    .toFormatter(Locale.ROOT)
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter ISO_DATE =
            DateTimeFormatter.ofPattern("uuuu-MM-dd", Locale.ROOT)
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter STORAGE_DATE_TIME =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_DATE_TIME =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a", Locale.ENGLISH);

    /** Prevents instantiation of this utility class. */
    private DateTimeParser() {
        // Utility class; do not instantiate.
    }

    /**
     * Parses a user-entered date or date/time.
     *
     * @param value the user-entered value.
     * @return the parsed date/time, using midnight for a date-only value.
     * @throws IllegalArgumentException if the value is empty or invalid.
     */
    public static LocalDateTime parseUserInput(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Date/time cannot be empty");
        }

        List<DateTimeFormatter> dateTimeFormats = List.of(
                DAY_MONTH_YEAR_TIME,
                ISO_DATE_TIME
        );
        for (DateTimeFormatter formatter : dateTimeFormats) {
            try {
                return LocalDateTime.parse(value.trim(), formatter);
            } catch (DateTimeParseException exception) {
                // Try the next supported format.
            }
        }

        try {
            return LocalDate.parse(value.trim(), ISO_DATE).atStartOfDay();
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException(
                    "Use d/M/yyyy HHmm or yyyy-MM-dd[ HHmm]", exception);
        }
    }

    /**
     * Parses a date/time stored in ISO format.
     *
     * @param value the stored date/time.
     * @return the parsed date/time.
     * @throws IllegalArgumentException if the stored value is invalid.
     */
    public static LocalDateTime parseStored(String value) {
        try {
            return LocalDateTime.parse(value.trim(), STORAGE_DATE_TIME);
        } catch (DateTimeParseException | NullPointerException exception) {
            throw new IllegalArgumentException("Invalid stored date/time", exception);
        }
    }

    /**
     * Formats a date/time for storage without losing precision.
     *
     * @param value the date/time to format.
     * @return the ISO storage representation.
     */
    public static String formatForStorage(LocalDateTime value) {
        return STORAGE_DATE_TIME.format(value);
    }

    /**
     * Formats a date/time for display to the user.
     *
     * @param value the date/time to format.
     * @return a readable date or date/time.
     */
    public static String formatForDisplay(LocalDateTime value) {
        if (value.toLocalTime().equals(java.time.LocalTime.MIDNIGHT)) {
            return DISPLAY_DATE.format(value);
        }
        return DISPLAY_DATE_TIME.format(value);
    }
}
