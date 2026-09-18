package geek.time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;

/**
 * Parses supported user-facing and stored date-time representations.
 *
 * User input is parsed strictly but case-insensitively after surrounding and
 * repeated whitespace is normalized.
 */
public final class DateTimeParser {
    /**
     * Supported patterns for user-entered date components.
     */
    private static final List<String> DATE_PATTERNS = List.of(
            "uuuu-MM-dd",
            "d/M/uuuu",
            "d-M-uuuu",
            "d MMM uuuu",
            "MMM d uuuu",
            "MMM d, uuuu"
    );

    /**
     * Supported patterns for user-entered time components.
     */
    private static final List<String> TIME_PATTERNS = List.of(
            "HHmm",
            "HH:mm",
            "h:mma",
            "h:mm a",
            "ha",
            "h a"
    );

    private static final List<DateTimeFormatter> DATE_FORMATTERS =
            createDateFormatters();

    private static final List<DateTimeFormatter> DATE_TIME_FORMATTERS =
            createDateTimeFormatters();

    private DateTimeParser() {
    }

    /**
     * Parses a deadline containing either a date or a date-time.
     *
     * A date-only deadline is represented at the start of that day with
     * {@code hasTime} set to {@code false}.
     *
     * @param input User-entered deadline.
     * @return Parsed deadline and whether a time was supplied.
     * @throws DateTimeParseException If no supported format matches.
     */
    public static ParsedDateTime parseDeadline(String input) {
        String normalizedInput = normalize(input);

        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                return new ParsedDateTime(
                        LocalDateTime.parse(
                                normalizedInput,
                                formatter
                        ),
                        true
                );
            } catch (DateTimeParseException e) {
                continue;
            }
        }

        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return new ParsedDateTime(
                        LocalDate.parse(
                                normalizedInput,
                                formatter
                        ).atStartOfDay(),
                        false
                );
            } catch (DateTimeParseException e) {
                continue;
            }
        }

        throw invalidDateTime(input);
    }

    /**
     * Parses a user-entered date-time.
     *
     * @param input User-entered date-time.
     * @return Parsed date-time.
     * @throws DateTimeParseException If no supported date-time format matches.
     */
    public static LocalDateTime parseDateTime(String input) {
        String normalizedInput = normalize(input);

        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                return LocalDateTime.parse(
                        normalizedInput,
                        formatter
                );
            } catch (DateTimeParseException e) {
                continue;
            }
        }

        throw invalidDateTime(input);
    }

    /**
     * Parses a user-entered date without a time.
     *
     * @param input User-entered date.
     * @return Parsed date.
     * @throws DateTimeParseException If no supported date format matches.
     */
    public static LocalDate parseDate(String input) {
        String normalizedInput = normalize(input);

        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(
                        normalizedInput,
                        formatter
                );
            } catch (DateTimeParseException e) {
                continue;
            }
        }

        throw invalidDateTime(input);
    }

    /**
     * Parses a stored ISO date or ISO date-time while preserving its time flag.
     *
     * @param storedValue Stored deadline value.
     * @return Parsed deadline and whether the stored value includes a time.
     * @throws DateTimeParseException If the value is neither a valid ISO date
     *         nor a valid ISO date-time.
     */
    public static ParsedDateTime parseStoredDeadline(
            String storedValue
    ) {
        try {
            return new ParsedDateTime(
                    LocalDateTime.parse(storedValue),
                    true
            );
        } catch (DateTimeParseException e) {
            try {
                return new ParsedDateTime(
                        LocalDate.parse(storedValue)
                                .atStartOfDay(),
                        false
                );
            } catch (DateTimeParseException nestedException) {
                throw invalidDateTime(storedValue);
            }
        }
    }

    /**
     * Creates strict, case-insensitive formatters for every supported date
     * pattern.
     *
     * @return Unmodifiable list of date formatters.
     */
    private static List<DateTimeFormatter> createDateFormatters() {
        return DATE_PATTERNS.stream()
                .map(DateTimeParser::createFormatter)
                .toList();
    }

    /**
     * Creates formatters for every supported date-and-time pattern
     * combination.
     *
     * @return Unmodifiable list of date-time formatters.
     */
    private static List<DateTimeFormatter> createDateTimeFormatters() {
        return DATE_PATTERNS.stream()
                .flatMap(datePattern -> TIME_PATTERNS.stream()
                        .map(timePattern -> createFormatter(
                                datePattern + " " + timePattern
                        )))
                .toList();
    }

    /**
     * Creates a strict, case-insensitive English formatter.
     *
     * @param pattern Date or date-time pattern.
     * @return Formatter for the pattern.
     */
    private static DateTimeFormatter createFormatter(
            String pattern
    ) {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(pattern)
                .toFormatter(Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT);
    }

    /**
     * Trims an input and collapses repeated whitespace to one space.
     *
     * @param input Text to normalize.
     * @return Normalized text.
     */
    private static String normalize(String input) {
        return input.trim().replaceAll("\\s+", " ");
    }

    /**
     * Creates the common exception used when no supported format matches.
     *
     * @param input Input that could not be parsed.
     * @return Date-time parsing exception for the input.
     */
    private static DateTimeParseException invalidDateTime(
            String input
    ) {
        return new DateTimeParseException(
                "Unsupported date or time format.",
                input,
                0
        );
    }

    /**
     * Represents a parsed deadline and whether its source included a time.
     *
     * @param value Parsed value; date-only inputs use the start of the day.
     * @param hasTime Whether the source included a time component.
     */
    public record ParsedDateTime(
            LocalDateTime value,
            boolean hasTime
    ) {
    }
}
