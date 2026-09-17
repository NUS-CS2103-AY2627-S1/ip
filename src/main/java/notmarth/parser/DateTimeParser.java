package notmarth.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Parses and formats the date and time values used by NotMarth tasks.
 */
public final class DateTimeParser {
    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd uuuu h:mm a", Locale.ENGLISH);
    private static final List<DateTimeFormatter> DATE_TIME_FORMATTERS = List.of(
            formatter("d/M/uuuu HHmm"),
            formatter("d/M/uuuu H:mm"),
            formatter("uuuu-M-d HHmm"),
            formatter("uuuu-M-d H:mm"),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    private static final List<DateTimeFormatter> DATE_FORMATTERS = List.of(
            formatter("d/M/uuuu"),
            formatter("uuuu-M-d"),
            DateTimeFormatter.ISO_LOCAL_DATE);

    private DateTimeParser() {
        // Utility class; do not create instances.
    }

    /**
     * Parses a date-only or date-time value.
     *
     * @param input the value entered by the user or loaded from storage.
     * @return the parsed value and whether the input included a time.
     * @throws DateTimeParseException if the value is not supported.
     */
    public static ParsedDateTime parse(String input) {
        if (input == null || input.isBlank() || !input.equals(input.trim())
                || input.matches(".*\\s{2,}.*")) {
            String reportedInput = input == null ? "" : input;
            throw new DateTimeParseException("Date or time contains invalid whitespace", reportedInput, 0);
        }
        Optional<ParsedDateTime> dateTime = DATE_TIME_FORMATTERS.stream()
                .map(formatter -> tryParseDateTime(input, formatter))
                .flatMap(Optional::stream)
                .findFirst();
        if (dateTime.isPresent()) {
            return dateTime.get();
        }

        return DATE_FORMATTERS.stream()
                .map(formatter -> tryParseDate(input, formatter))
                .flatMap(Optional::stream)
                .findFirst()
                .orElseThrow(() -> new DateTimeParseException(
                        "Unsupported date or time format", input, 0));
    }

    private static Optional<ParsedDateTime> tryParseDateTime(String input, DateTimeFormatter formatter) {
        try {
            return Optional.of(new ParsedDateTime(LocalDateTime.parse(input, formatter), true));
        } catch (DateTimeParseException exception) {
            return Optional.empty();
        }
    }

    private static Optional<ParsedDateTime> tryParseDate(String input, DateTimeFormatter formatter) {
        try {
            return Optional.of(new ParsedDateTime(LocalDate.parse(input, formatter).atStartOfDay(), false));
        } catch (DateTimeParseException exception) {
            return Optional.empty();
        }
    }

    /**
     * Parses a date without accepting a time component.
     *
     * @param input the date entered by the user.
     * @return the parsed calendar date.
     * @throws DateTimeParseException if the value is not a supported date-only format.
     */
    public static LocalDate parseDate(String input) {
        ParsedDateTime parsedDateTime = parse(input);
        if (parsedDateTime.hasTime()) {
            throw new DateTimeParseException("A date-only value is required", input, 0);
        }
        return parsedDateTime.value().toLocalDate();
    }

    /**
     * Formats a parsed value for the console.
     *
     * @param value the date and time to format.
     * @param hasTime whether the original value included a time.
     * @return the readable console representation.
     */
    public static String format(LocalDateTime value, boolean hasTime) {
        return (hasTime ? DISPLAY_DATE_TIME_FORMATTER : DISPLAY_DATE_FORMATTER).format(value);
    }

    /**
     * Formats a value for the unambiguous task archive.
     *
     * @param value the date and time to format.
     * @param hasTime whether the original value included a time.
     * @return an ISO date or ISO local date-time.
     */
    public static String formatForStorage(LocalDateTime value, boolean hasTime) {
        return hasTime ? value.toString() : value.toLocalDate().toString();
    }

    /**
     * Creates a strict, English-locale formatter for one supported input pattern.
     *
     * @param pattern the date or date-time pattern.
     * @return a formatter that rejects invalid calendar values.
     */
    private static DateTimeFormatter formatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT);
    }

    /**
     * Contains a parsed value and whether the source included a time.
     *
     * @param value the parsed date and time, at midnight for date-only input.
     * @param hasTime whether the source text included a time component.
     */
    public record ParsedDateTime(LocalDateTime value, boolean hasTime) {
    }
}
