package caitlyn;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Locale;

/**
 * Parses and formats the date and time values used by dated tasks.
 */
public final class DateTimeParser {
    /** Format used when displaying dates without a time. */
    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("MMM d uuuu", Locale.ENGLISH);

    /** Format used when displaying dates that include a time. */
    private static final DateTimeFormatter DISPLAY_DATE_TIME =
            DateTimeFormatter.ofPattern("MMM d uuuu h:mm a", Locale.ENGLISH);

    /** Shows all significant seconds and fractional seconds instead of silently hiding them. */
    private static final DateTimeFormatter DISPLAY_PRECISE_DATE_TIME = new DateTimeFormatterBuilder()
            .appendPattern("MMM d uuuu h:mm:ss")
            .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
            .appendPattern(" a")
            .toFormatter(Locale.ENGLISH);

    /** Formats accepted when a command includes a time. */
    private static final List<DateTimeFormatter> DATE_TIME_FORMATTERS = List.of(
            createFormatter("uuuu-MM-dd HHmm"),
            createFormatter("d/M/uuuu HHmm"),
            createFormatter("uuuu-MM-dd HH:mm"),
            createFormatter("d/M/uuuu HH:mm"));

    /** Formats accepted when a command contains only a date. */
    private static final List<DateTimeFormatter> DATE_FORMATTERS = List.of(
            createFormatter("uuuu-MM-dd"),
            createFormatter("d/M/uuuu"));

    /**
     * Prevents construction of this utility class.
     */
    private DateTimeParser() {
    }

    /**
     * Parses a command date or date/time.
     *
     * @param text the date or date/time entered by the user.
     * @return the parsed value and whether the input included a time.
     * @throws IllegalArgumentException if the input is not a supported date format.
     */
    public static ParsedDateTime parse(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("A date or time cannot be empty.");
        }
        text = text.trim().replaceAll("[ \\t]+", " ");

        try {
            return new ParsedDateTime(LocalDateTime.parse(text), true);
        } catch (DateTimeParseException exception) {
            // Try the command-friendly formats below.
        }

        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                return new ParsedDateTime(LocalDateTime.parse(text, formatter), true);
            } catch (DateTimeParseException exception) {
                // Try the next supported format.
            }
        }

        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return new ParsedDateTime(LocalDate.parse(text, formatter).atStartOfDay(), false);
            } catch (DateTimeParseException exception) {
                // Try the next supported format.
            }
        }

        throw new IllegalArgumentException(
                "Use yyyy-MM-dd, yyyy-MM-dd HHmm, or d/M/yyyy HHmm for dates and times.");
    }

    /**
     * Formats a parsed value for the task list.
     *
     * @param parsedDateTime the parsed date/time value.
     * @return a readable date or date/time string.
     */
    public static String formatForDisplay(ParsedDateTime parsedDateTime) {
        return formatForDisplay(parsedDateTime.value(), parsedDateTime.hasTime());
    }

    /**
     * Formats a local date/time for display while preserving whether the user supplied a time.
     *
     * @param value the local date/time value.
     * @param hasTime whether the original input included a time.
     * @return a readable date or date/time string.
     */
    public static String formatForDisplay(LocalDateTime value, boolean hasTime) {
        if (hasTime && (value.getSecond() != 0 || value.getNano() != 0)) {
            return value.format(DISPLAY_PRECISE_DATE_TIME);
        }
        DateTimeFormatter formatter = hasTime ? DISPLAY_DATE_TIME : DISPLAY_DATE;
        return value.format(formatter);
    }

    /**
     * Formats a parsed value for persistence in the task file.
     *
     * @param parsedDateTime the parsed date/time value.
     * @return a canonical ISO date or date/time string.
     */
    public static String formatForStorage(ParsedDateTime parsedDateTime) {
        return formatForStorage(parsedDateTime.value(), parsedDateTime.hasTime());
    }

    /**
     * Formats a local date/time for persistence while preserving date-only values.
     *
     * @param value the local date/time value.
     * @param hasTime whether the original input included a time.
     * @return a canonical ISO date or date/time string.
     */
    public static String formatForStorage(LocalDateTime value, boolean hasTime) {
        return hasTime ? value.toString() : value.toLocalDate().toString();
    }

    /**
     * Creates a strict formatter using the proleptic-year pattern required by {@code java.time}.
     *
     * @param pattern the accepted input pattern.
     * @return a strict English formatter.
     */
    private static DateTimeFormatter createFormatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT);
    }

    /**
     * Represents a parsed date/time and whether the user supplied a time.
     *
     * @param value the parsed local date/time.
     * @param hasTime whether the original input included a time.
     */
    public record ParsedDateTime(LocalDateTime value, boolean hasTime) {
        /**
         * Creates a parsed date/time result.
         *
         * @param value the parsed local date/time.
         * @param hasTime whether the original input included a time.
         */
        public ParsedDateTime {
            if (value == null) {
                throw new IllegalArgumentException("A parsed date/time cannot be null.");
            }
        }
    }
}
