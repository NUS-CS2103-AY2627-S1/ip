package charliek.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;

/**
 * Parses, formats, and serializes the date/time values used by dated tasks.
 */
public final class DateTimeParser {
    private static final Locale DISPLAY_LOCALE = Locale.ENGLISH;

    /**
     * Formats dates in the user-facing task list.
     */
    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("d MMM uuuu", DISPLAY_LOCALE);

    /**
     * Formats date-times in the user-facing task list.
     */
    private static final DateTimeFormatter DISPLAY_DATE_TIME =
            DateTimeFormatter.ofPattern("d MMM uuuu, HH:mm", DISPLAY_LOCALE);

    /**
     * Date patterns accepted from users, ordered from numeric to textual forms.
     */
    private static final List<String> DATE_PATTERNS = List.of(
            "d/M/uuuu", "d-M-uuuu", "d.M.uuuu",
            "uuuu-M-d", "uuuu/M/d", "uuuu.M.d",
            "d/M/uu", "d-M-uu", "d.M.uu",
            "d MMM uuuu", "d MMMM uuuu", "d-MMM-uuuu", "d-MMMM-uuuu",
            "MMM d uuuu", "MMMM d uuuu", "uuuu MMM d");

    /**
     * Time patterns accepted from users.
     */
    private static final List<String> TIME_PATTERNS = List.of(
            "HHmm", "H:mm", "HH:mm", "H:mm:ss", "HH:mm:ss",
            "ha", "h a", "h:mma", "h:mm a", "h:mm:ssa", "h:mm:ss a",
            "h.mma", "h.mm a");

    /**
     * Separators accepted between a date and a time.
     */
    private static final List<String> DATE_TIME_SEPARATORS = List.of(" ", ", ", "T");

    private DateTimeParser() {
        // Utility class.
    }

    /**
     * Parses a user-entered date or date-time and detects whether a time was supplied.
     *
     * @param input the date or date-time entered by the user.
     * @return the parsed date or date-time.
     * @throws DateTimeParseException if no supported format matches.
     */
    public static ParsedDateTime parseUserInput(String input) {
        String normalizedInput = normalize(input);
        if (normalizedInput.isEmpty()) {
            throw new DateTimeParseException("The date/time is empty.", normalizedInput, 0);
        }

        for (String datePattern : DATE_PATTERNS) {
            for (String timePattern : TIME_PATTERNS) {
                for (String separator : DATE_TIME_SEPARATORS) {
                    try {
                        DateTimeFormatter formatter = formatterFor(datePattern, separator, timePattern);
                        return ParsedDateTime.ofDateTime(LocalDateTime.parse(normalizedInput, formatter));
                    } catch (DateTimeParseException ignored) {
                        // Try the next supported combination.
                    }
                }
            }
        }

        for (String datePattern : DATE_PATTERNS) {
            try {
                DateTimeFormatter formatter = formatterFor(datePattern);
                return ParsedDateTime.ofDate(LocalDate.parse(normalizedInput, formatter));
            } catch (DateTimeParseException ignored) {
                // Try the next supported date format.
            }
        }

        throw new DateTimeParseException(
                "Unsupported date/time format. Try 2/12/2019 or 2/12/2019 6pm.",
                normalizedInput, 0);
    }

    /**
     * Parses the canonical ISO date or date-time value stored in the CSV file.
     *
     * @param storedValue the stored ISO date or date-time value.
     * @return the parsed date or date-time.
     * @throws DateTimeParseException if the stored value is not a valid ISO value.
     */
    public static ParsedDateTime parseStored(String storedValue) {
        String normalizedValue = normalize(storedValue);
        if (normalizedValue.contains("T")) {
            return ParsedDateTime.ofDateTime(LocalDateTime.parse(
                    normalizedValue, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
        return ParsedDateTime.ofDate(LocalDate.parse(
                normalizedValue, DateTimeFormatter.ISO_LOCAL_DATE));
    }

    /**
     * Formats a parsed value for display in a task description.
     *
     * @param value the parsed date or date-time to format.
     * @return the user-facing date or date-time text.
     * @throws IllegalArgumentException if {@code value} is {@code null}.
     */
    public static String formatForDisplay(ParsedDateTime value) {
        requireValue(value);
        if (value.hasTime()) {
            return DISPLAY_DATE_TIME.format(value.dateTime());
        }
        return DISPLAY_DATE.format(value.date());
    }

    /**
     * Formats a parsed value for stable CSV persistence.
     *
     * @param value the parsed date or date-time to format.
     * @return the canonical ISO date or date-time text.
     * @throws IllegalArgumentException if {@code value} is {@code null}.
     */
    public static String formatForStorage(ParsedDateTime value) {
        requireValue(value);
        if (value.hasTime()) {
            return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(value.dateTime());
        }
        return DateTimeFormatter.ISO_LOCAL_DATE.format(value.date());
    }

    /**
     * Creates a strict formatter for a date-only pattern.
     */
    private static DateTimeFormatter formatterFor(String datePattern) {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(datePattern)
                .toFormatter(DISPLAY_LOCALE)
                .withResolverStyle(ResolverStyle.STRICT);
    }

    /**
     * Creates a strict formatter for a date pattern followed by a time pattern.
     */
    private static DateTimeFormatter formatterFor(
            String datePattern, String separator, String timePattern) {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(datePattern)
                .appendLiteral(separator)
                .appendPattern(timePattern)
                .toFormatter(DISPLAY_LOCALE)
                .withResolverStyle(ResolverStyle.STRICT);
    }

    /**
     * Normalizes optional input whitespace before parsing.
     */
    private static String normalize(String input) {
        if (input == null) {
            return "";
        }
        return input.trim().replaceAll("\\s+", " ");
    }

    /**
     * Rejects a missing parsed date/time value before formatting.
     */
    private static void requireValue(ParsedDateTime value) {
        if (value == null) {
            throw new IllegalArgumentException("A date/time value is required.");
        }
    }

    /**
     * Holds either a date-only value or a value that includes a time.
     *
     * @param date the date-only value, or {@code null} when a time is present.
     * @param dateTime the date-time value, or {@code null} for a date-only value.
     */
    public record ParsedDateTime(LocalDate date, LocalDateTime dateTime) {
        /**
         * Validates that exactly one of the date components is present.
         *
         * @param date the date-only value, or {@code null} when a time is present.
         * @param dateTime the date-time value, or {@code null} for a date-only value.
         * @throws IllegalArgumentException if both values or neither value is supplied.
         */
        public ParsedDateTime {
            if ((date == null) == (dateTime == null)) {
                throw new IllegalArgumentException("Exactly one date value must be supplied.");
            }
        }

        /**
         * Creates a date-only parsed value.
         *
         * @param date the date to store.
         * @return a parsed value containing the supplied date.
         */
        public static ParsedDateTime ofDate(LocalDate date) {
            return new ParsedDateTime(date, null);
        }

        /**
         * Creates a date-time parsed value.
         *
         * @param dateTime the date-time to store.
         * @return a parsed value containing the supplied date-time.
         */
        public static ParsedDateTime ofDateTime(LocalDateTime dateTime) {
            return new ParsedDateTime(null, dateTime);
        }

        /**
         * Checks whether this value includes a time.
         *
         * @return {@code true} when this value contains a date-time.
         */
        public boolean hasTime() {
            return dateTime != null;
        }
    }
}
