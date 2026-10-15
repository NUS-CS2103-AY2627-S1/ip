package milo.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/** Parses Milo's supported date/time inputs and formats stored values. */
public final class DateTimeParser {
    private static final DateTimeFormatter ISO_DATE =
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DAY_MONTH_YEAR =
            DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter ISO_DATE_TIME =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DAY_MONTH_YEAR_TIME =
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DISPLAY_DATE_TIME =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter STORAGE_DATE_TIME =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm");

    /** Prevents construction of this utility class. */
    private DateTimeParser() {
        // Utility class; do not instantiate.
    }

    /**
     * Parses an ISO or day/month/year date, optionally followed by a 24-hour time.
     *
     * @param input date/time text such as {@code 2019-10-15 1800}
     * @return the parsed date/time, using midnight for date-only input
     * @throws DateTimeParseException if the input is empty or has an unsupported value
     */
    public static LocalDateTime parse(String input) {
        String value = input == null ? "" : input.trim();
        if (value.isEmpty()) {
            throw new DateTimeParseException("Date cannot be empty", value, 0);
        }

        DateTimeFormatter[] dateTimeFormats = {ISO_DATE_TIME, DAY_MONTH_YEAR_TIME};
        for (DateTimeFormatter format : dateTimeFormats) {
            try {
                return LocalDateTime.parse(value, format);
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }

        DateTimeFormatter[] dateFormats = {ISO_DATE, DAY_MONTH_YEAR};
        for (DateTimeFormatter format : dateFormats) {
            try {
                return LocalDate.parse(value, format).atStartOfDay();
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }

        throw new DateTimeParseException(
                "Expected yyyy-MM-dd [HHmm] or d/M/yyyy [HHmm]", value, 0);
    }

    /**
     * Formats a date/time for display in a human-readable form.
     *
     * @param dateTime date/time to format
     * @return formatted date/time text
     */
    public static String formatForDisplay(LocalDateTime dateTime) {
        // Date-only input is stored at midnight and is shown without a distracting time.
        return dateTime.toLocalTime().equals(LocalTime.MIDNIGHT)
                ? DISPLAY_DATE.format(dateTime)
                : DISPLAY_DATE_TIME.format(dateTime);
    }

    /**
     * Formats a date/time in the stable representation used by storage.
     *
     * @param dateTime date/time to format
     * @return date/time text in {@code yyyy-MM-dd HHmm} format
     */
    public static String formatForStorage(LocalDateTime dateTime) {
        return STORAGE_DATE_TIME.format(dateTime);
    }
}
