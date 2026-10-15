package kpei.utility;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;

import kpei.exceptions.BertException;

/**
 * Parses date and time strings independently and formats them for display.
 * Dates and times are stored in their ISO-8601 {@link Object#toString()} forms.
 */
public class DateTimeParser {

    /** Formatter for displaying dates to users. */
    public static final DateTimeFormatter DISPLAY_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM uuuu", Locale.ENGLISH);

    /** Formatter for displaying times to users. */
    public static final DateTimeFormatter DISPLAY_TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a");

    /** Date formats accepted from users and storage. */
    private static final List<DateTimeFormatter> DATE_FORMATTERS = List.of(
            createFormatter("uuuu-M-d"),
            createFormatter("uuuu/M/d"),
            createFormatter("uuuu.M.d"),

            createFormatter("d/M/uuuu"),
            createFormatter("d-M-uuuu"),
            createFormatter("d.M.uuuu"),
            createFormatter("d MMM uuuu"),
            createFormatter("d MMMM uuuu"),

            createFormatter("M/d/uuuu"),
            createFormatter("M-d-uuuu"),
            createFormatter("M.d.uuuu"),
            createFormatter("MMM d uuuu"),
            createFormatter("MMMM d uuuu"),

            DateTimeFormatter.ISO_LOCAL_DATE
    );

    /** Time formats accepted from users and storage. */
    private static final List<DateTimeFormatter> TIME_FORMATTERS = List.of(
            createFormatter("HHmm"),

            createFormatter("H:mm"),
            createFormatter("H:mm:ss"),
            createFormatter("h:mm a"),
            createFormatter("hh:mm a"),
            createFormatter("h:mma"),
            createFormatter("hh:mma"),

            createFormatter("H.mm"),
            createFormatter("H.mm.ss"),
            createFormatter("h.mm a"),
            createFormatter("hh.mm a"),
            createFormatter("h.mma"),
            createFormatter("hh.mma"),
            
            DateTimeFormatter.ISO_LOCAL_TIME
    );

    /**
     * Parses a supported date format.
     *
     * @param input The raw date string.
     * @return The parsed date.
     * @throws BertException If the input is blank or does not match a supported date format.
     */
    public static LocalDate parseDate(String input) throws BertException {
        validateInput(input, "Date");

        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(input.trim(), formatter);
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }

        throw new BertException("Invalid date: " + input);
    }

    /**
     * Parses a supported time format.
     *
     * @param input The raw time string.
     * @return The parsed time.
     * @throws BertException If the input is blank or does not match a supported time format.
     */
    public static LocalTime parseTime(String input) throws BertException {
        validateInput(input, "Time");

        for (DateTimeFormatter formatter : TIME_FORMATTERS) {
            try {
                return LocalTime.parse(input.trim(), formatter);
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }

        throw new BertException("Invalid time: " + input);
    }

    /**
     * Formats a date for user display.
     *
     * @param date The date to format.
     * @return A user-friendly date string.
     */
    public static String formatDate(LocalDate date) {
        return date.format(DISPLAY_DATE_FORMATTER);
    }

    /**
     * Formats a time for user display.
     *
     * @param time The time to format.
     * @return A user-friendly time string.
     */
    public static String formatTime(LocalTime time) {
        return time.format(DISPLAY_TIME_FORMATTER);
    }

    /**
     * Validates that an input string contains a value.
     *
     * @param input The input string to validate.
     * @param valueName The human-readable name of the value.
     * @throws BertException If the input is blank.
     */
    private static void validateInput(String input, String valueName) throws BertException {
        if (input == null || input.isBlank()) {
            throw new BertException(valueName + " string cannot be empty.");
        }
    }

    /**
     * Creates a strict, case-insensitive English formatter.
     *
     * @param pattern The formatter pattern.
     * @return The configured formatter.
     */
    private static DateTimeFormatter createFormatter(String pattern) {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(pattern)
                .toFormatter(Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT);
    }
}
