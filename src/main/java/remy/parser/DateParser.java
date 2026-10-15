package remy.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;

/**
 * Parses dates and date-times written in the formats supported by Remy.
 *
 * @author LEE-wz
 */
public final class DateParser {
    /** Supported date-time input formats. */
    private static final List<DateTimeFormatter> DATE_TIME_FORMATTERS = List.of(
            createStrictFormatter("d/M/uuuu HHmm"),
            createStrictFormatter("d/M/uuuu HH:mm"),
            createStrictFormatter("dd/MM/uuuu HHmm"),
            createStrictFormatter("dd/MM/uuuu HH:mm"),
            createStrictFormatter("d-M-uuuu HHmm"),
            createStrictFormatter("d-M-uuuu HH:mm"),
            createStrictFormatter("dd-MM-uuuu HHmm"),
            createStrictFormatter("dd-MM-uuuu HH:mm"),
            createStrictFormatter("uuuu-MM-dd HHmm"),
            createStrictFormatter("uuuu-MM-dd HH:mm"),
            createStrictFormatter("uuuu-M-d HHmm"),
            createStrictFormatter("uuuu-M-d HH:mm"),
            createStrictFormatter("MMM dd uuuu HHmm"),
            createStrictFormatter("MMM dd uuuu HH:mm"),
            createStrictFormatter("dd MMM uuuu HHmm"),
            createStrictFormatter("dd MMM uuuu HH:mm"),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME);

    /** Supported date input formats. */
    private static final List<DateTimeFormatter> DATE_FORMATTERS = List.of(
            createStrictFormatter("d/M/uuuu"),
            createStrictFormatter("dd/MM/uuuu"),
            createStrictFormatter("d-M-uuuu"),
            createStrictFormatter("dd-MM-uuuu"),
            createStrictFormatter("uuuu-MM-dd"),
            createStrictFormatter("uuuu-M-d"),
            createStrictFormatter("MMM dd uuuu"),
            createStrictFormatter("dd MMM uuuu"),
            DateTimeFormatter.ISO_LOCAL_DATE);

    /** Prevents instantiation of this utility class. */
    private DateParser() {
    }

    /**
     * Creates a locale-stable formatter that rejects impossible dates and times.
     *
     * @param pattern date or date-time pattern to use
     * @return strict English-language formatter
     */
    private static DateTimeFormatter createStrictFormatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT);
    }

    /**
     * Parses a date-time string using the supported date-time formats.
     *
     * @param value date-time string to parse
     * @return the parsed date-time, or null when no supported format matches
     */
    public static LocalDateTime parseDateTime(String value) {
        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                return LocalDateTime.parse(value, formatter);
            } catch (DateTimeParseException ignored) {
                // Try the next supported date-time format.
            }
        }
        return null;
    }

    /**
     * Parses a date string using the supported date formats.
     *
     * @param value date string to parse
     * @return the parsed date, or null when no supported format matches
     */
    public static LocalDate parseDate(String value) {
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(value, formatter);
            } catch (DateTimeParseException ignored) {
                // Try the next supported date format.
            }
        }
        return null;
    }
}
