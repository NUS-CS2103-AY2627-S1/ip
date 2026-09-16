package panda.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;

/**
 * Parses and formats the date/time values used by deadline and event tasks.
 */
public final class DateTimeUtil {
    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_TIME =
            DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
    private static final DateTimeFormatter STORAGE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final List<DateTimeFormatter> DATE_TIME_FORMATS = List.of(
            strictFormat("d/M/uuuu HHmm"),
            strictFormat("uuuu-MM-dd HHmm"),
            strictFormat("d/M/uuuu HH:mm"),
            strictFormat("uuuu-MM-dd HH:mm"),
            new DateTimeFormatterBuilder().parseCaseInsensitive()
                    .appendPattern("d/M/uuuu h[:mm] a")
                    .toFormatter(Locale.ENGLISH).withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    private static final List<DateTimeFormatter> DATE_FORMATS = List.of(
            strictFormat("d/M/uuuu"),
            strictFormat("uuuu-MM-dd"),
            DateTimeFormatter.ISO_LOCAL_DATE);

    private DateTimeUtil() {
        // Utility class.
    }

    /**
     * Parses a date or date/time, accepting day/month/year and ISO-style input.
     *
     * @param value user-provided date or date/time text
     * @return parsed value, with date-only input set to midnight
     * @throws DateTimeParseException if the value is missing, malformed, or impossible
     */
    public static LocalDateTime parse(String value) {
        if (value == null || value.isBlank()) {
            throw new DateTimeParseException("Date/time cannot be empty", value == null ? "" : value, 0);
        }

        String normalized = value.trim().replaceAll("\\s+", " ");
        for (DateTimeFormatter formatter : DATE_TIME_FORMATS) {
            try {
                return LocalDateTime.parse(normalized, formatter);
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }
        for (DateTimeFormatter formatter : DATE_FORMATS) {
            try {
                return LocalDate.parse(normalized, formatter).atStartOfDay();
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }
        throw new DateTimeParseException("Unsupported date/time format", value, 0);
    }

    /**
     * Formats a value for Panda's user-facing task display.
     *
     * @param value date/time to format
     * @return date with a time appended when the input contains a non-midnight time
     */
    public static String formatForDisplay(LocalDateTime value) {
        String date = value.format(DISPLAY_DATE);
        return value.toLocalTime().equals(java.time.LocalTime.MIDNIGHT)
                ? date : date + " " + value.format(DISPLAY_TIME);
    }

    /**
     * Formats a value canonically for storage and later parsing.
     *
     * @param value date/time to format
     * @return ISO local date/time text
     */
    public static String formatForStorage(LocalDateTime value) {
        return value.format(STORAGE_FORMAT);
    }

    /**
     * Builds a formatter that rejects invalid date/time values instead of accepting them silently.
     *
     * @param pattern the pattern to compile
     * @return a strict formatter for the given pattern
     */
    private static DateTimeFormatter strictFormat(String pattern) {
        return DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT);
    }
}
