package bo.time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Parses and formats the date and time values used by deadline and event tasks.
 */
public final class DateTimeParser {
    /** Formatter for the slash date-time form used in the example command. */
    private static final DateTimeFormatter SLASH_DATE_TIME = formatter("d/M/uuuu HHmm");

    /** Formatter for an ISO date followed by a compact time. */
    private static final DateTimeFormatter ISO_COMPACT_DATE_TIME = formatter("uuuu-MM-dd HHmm");

    /** Formatter for an ISO date followed by a colon-separated time. */
    private static final DateTimeFormatter ISO_COLON_DATE_TIME = formatter("uuuu-MM-dd HH:mm");

    /** Formatter for a slash date without a time. */
    private static final DateTimeFormatter SLASH_DATE = formatter("d/M/uuuu");

    /** Formatter used when showing a date-only value to the user. */
    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);

    /** Formatter used when showing a date and time to the user. */
    private static final DateTimeFormatter DISPLAY_DATE_TIME =
            DateTimeFormatter.ofPattern("MMM dd uuuu HH:mm", Locale.ENGLISH);

    /** Formatter used to persist a date-only value. */
    private static final DateTimeFormatter STORAGE_DATE = DateTimeFormatter.ISO_LOCAL_DATE;

    /** Matches values that look like one of Bo's structured date/time formats. */
    private static final Pattern STRUCTURED_DATE_TIME = Pattern.compile(
            "^(?:\\d{1,2}/\\d{1,2}/\\d{4}(?:\\s+\\d{4})?"
                    + "|\\d{4}-\\d{1,2}-\\d{1,2}"
                    + "(?:(?:\\s+|T)\\d{1,2}:?\\d{2}(?::\\d{2})?)?)$");

    private DateTimeParser() {
        // Utility class; do not instantiate.
    }

    /**
     * Parses a supported date or date-time string.
     *
     * <p>Supported examples include {@code 2019-10-15},
     * {@code 2/12/2019 1800}, {@code 2019-10-15 1800}, and
     * {@code 2019-10-15 18:00}. The slash form uses day/month/year order.</p>
     *
     * @param input the date or date-time entered by the user.
     * @return the parsed value and whether the input included a time.
     * @throws IllegalArgumentException if the input is not a supported value.
     */
    public static ParsedDateTime parse(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("A date or time cannot be blank.");
        }

        String value = input.strip().replaceAll("\\s+", " ");
        for (DateTimeFormatter dateTimeFormatter : List.of(
                SLASH_DATE_TIME,
                ISO_COMPACT_DATE_TIME,
                ISO_COLON_DATE_TIME,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME)) {
            try {
                return new ParsedDateTime(LocalDateTime.parse(value, dateTimeFormatter), true);
            } catch (DateTimeParseException exception) {
                // Try the next accepted input format.
            }
        }

        for (DateTimeFormatter dateFormatter : List.of(
                DateTimeFormatter.ISO_LOCAL_DATE,
                SLASH_DATE)) {
            try {
                return new ParsedDateTime(LocalDate.parse(value, dateFormatter).atStartOfDay(), false);
            } catch (DateTimeParseException exception) {
                // Try the next accepted input format.
            }
        }

        throw new IllegalArgumentException("Unsupported date/time format: " + input);
    }

    /**
     * Returns whether a value appears to be a structured date/time rather than
     * a legacy free-form value such as {@code Friday}.
     *
     * <p>This distinction lets Bo continue displaying old free-form values
     * while rejecting malformed values that clearly intend a numeric date or
     * time format.</p>
     *
     * @param input the value to inspect.
     * @return {@code true} when the value resembles a structured format.
     */
    public static boolean looksLikeStructuredDateTime(String input) {
        return input != null && STRUCTURED_DATE_TIME.matcher(input.strip()).matches();
    }

    /**
     * Formats a parsed value for display in Bo's responses.
     *
     * @param parsedDateTime the value to format.
     * @return a human-readable date or date-time.
     */
    public static String format(ParsedDateTime parsedDateTime) {
        if (parsedDateTime.includesTime()) {
            return DISPLAY_DATE_TIME.format(parsedDateTime.value());
        }
        return DISPLAY_DATE.format(parsedDateTime.value());
    }

    /**
     * Formats a parsed value in a stable form for the storage file.
     *
     * @param parsedDateTime the value to serialize.
     * @return an ISO date or ISO local date-time.
     */
    public static String serialize(ParsedDateTime parsedDateTime) {
        if (parsedDateTime.includesTime()) {
            return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(parsedDateTime.value());
        }
        return STORAGE_DATE.format(parsedDateTime.value());
    }

    /**
     * Creates a strict formatter for a pattern containing a year.
     *
     * @param pattern the date/time pattern.
     * @return the strict formatter.
     */
    private static DateTimeFormatter formatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT);
    }

    /**
     * A typed date/time value together with whether the user supplied a time.
     *
     * @param value the parsed date and time, with date-only values at midnight.
     * @param includesTime whether the original input included an explicit time.
     */
    public record ParsedDateTime(LocalDateTime value, boolean includesTime) {
    }
}
