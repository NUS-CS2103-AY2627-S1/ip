package trybot.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Parses the date and date-time formats understood by TryBot.
 */
public final class DateTimeParser {
    private static final List<DateTimeFormatter> DATE_TIME_FORMATTERS = List.of(
            formatter("uuuu-MM-dd HHmm"),
            formatter("uuuu-MM-dd HH:mm"),
            formatter("d/M/uuuu HHmm"),
            formatter("d/M/uuuu HH:mm"),
            formatter("d-M-uuuu HHmm"),
            formatter("d-M-uuuu HH:mm"));

    private static final List<DateTimeFormatter> DATE_FORMATTERS = List.of(
            formatter("uuuu-MM-dd"),
            formatter("d/M/uuuu"),
            formatter("d-M-uuuu"));

    private static final DateTimeFormatter STORAGE_DATE_FORMATTER = formatter("uuuu-MM-dd");
    private static final DateTimeFormatter STORAGE_DATE_TIME_FORMATTER = formatter("uuuu-MM-dd HHmm");
    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER = formatter("MMM dd uuuu");
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMATTER = formatter("MMM dd uuuu HH:mm");
    private static final Pattern NUMERIC_DATE_PATTERN = Pattern.compile(
            "\\s*\\d{1,4}[-/]\\d{1,2}[-/]\\d{1,4}(?:\\s+.*)?\\s*");

    private DateTimeParser() {
        // Utility class; do not instantiate.
    }

    /**
     * Parses a supported date or date-time, or returns null for legacy text such as "Friday".
     *
     * @param value user-provided date or time text.
     * @return parsed value, or null when the value is ordinary descriptive text
     * @throws IllegalArgumentException when the value looks numeric but is not a valid date
     */
    public static ParsedDateTime parseOrNull(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Date or time cannot be null.");
        }
        String trimmedValue = value.trim();
        if (trimmedValue.isEmpty()) {
            throw new IllegalArgumentException("Date or time cannot be blank.");
        }
        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                return new ParsedDateTime(LocalDateTime.parse(trimmedValue, formatter), true);
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                LocalDate date = LocalDate.parse(trimmedValue, formatter);
                return new ParsedDateTime(date.atStartOfDay(), false);
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }
        if (NUMERIC_DATE_PATTERN.matcher(trimmedValue).matches()) {
            throw new IllegalArgumentException("Invalid date or time: " + trimmedValue
                    + ". Use yyyy-mm-dd or d/M/yyyy HHmm.");
        }
        return null;
    }

    /**
     * Formats a parsed value for display in the task list.
     *
     * @param parsedDateTime parsed date or date-time.
     * @return human-readable date text
     */
    public static String formatForDisplay(ParsedDateTime parsedDateTime) {
        requireParsedValue(parsedDateTime);
        DateTimeFormatter formatter = parsedDateTime.hasTime()
                ? DISPLAY_DATE_TIME_FORMATTER : DISPLAY_DATE_FORMATTER;
        return parsedDateTime.dateTime().format(formatter);
    }

    /**
     * Formats a parsed value for persistence.
     *
     * @param parsedDateTime parsed date or date-time.
     * @return canonical date text
     */
    public static String formatForStorage(ParsedDateTime parsedDateTime) {
        requireParsedValue(parsedDateTime);
        DateTimeFormatter formatter = parsedDateTime.hasTime()
                ? STORAGE_DATE_TIME_FORMATTER : STORAGE_DATE_FORMATTER;
        return parsedDateTime.dateTime().format(formatter);
    }

    /**
     * Creates a strict English-locale formatter for a supported date pattern.
     *
     * @param pattern date or date-time pattern.
     * @return formatter that rejects invalid calendar values
     */
    private static DateTimeFormatter formatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT);
    }

    /**
     * Validates the value required by the formatting methods.
     *
     * @param parsedDateTime value to validate.
     * @throws IllegalArgumentException if the value or its date-time is null
     */
    private static void requireParsedValue(ParsedDateTime parsedDateTime) {
        if (parsedDateTime == null || parsedDateTime.dateTime() == null) {
            throw new IllegalArgumentException("A parsed date or time is required.");
        }
    }

    /**
     * Holds the typed representation of one parsed date or date-time.
     *
     * @param dateTime parsed date, with midnight used when no time was supplied.
     * @param hasTime whether the original value included a time.
     */
    public record ParsedDateTime(LocalDateTime dateTime, boolean hasTime) {
        /**
         * Creates a parsed date-time value after validating the date-time component.
         */
        public ParsedDateTime {
            if (dateTime == null) {
                throw new IllegalArgumentException("A parsed date or time cannot be null.");
            }
        }
    }
}
