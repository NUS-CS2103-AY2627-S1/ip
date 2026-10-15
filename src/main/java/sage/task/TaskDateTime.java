package sage.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Shares the date parsing and display rules used by deadlines and events.
 */
public final class TaskDateTime {
    private static final int MIN_YEAR = 1;
    private static final int MAX_YEAR = 9999;
    private static final String INVALID_TIME_MESSAGE = "That date or time is invalid. Use a real date such as "
            + "2026-09-18 or 18/9/2026 1430.";
    private static final Pattern NUMERIC_DATE_PREFIX = Pattern.compile("[+-]?\\p{Nd}+\\s*[/.-].*");
    private static final Pattern TWELVE_HOUR_TIME = Pattern.compile(
            "(?i)(?<![\\p{L}\\p{N}])([+-]?\\d+(?::\\d+)?\\s*[ap]m)(?!\\p{L})");
    private static final DateTimeFormatter CLOCK_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive().appendPattern("h[:mm]a").toFormatter(Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM d uuuu, h:mma", Locale.ENGLISH);
    private static final DateTimeFormatter DATE_ONLY_DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM d uuuu", Locale.ENGLISH);
    private static final List<DateTimeFormatter> INPUT_FORMATTERS = List.of(
            strictFormatter("d/M/uuuu HHmm"),
            strictFormatter("d/M/uuuu HH:mm"),
            strictFormatter("d/M/uuuu"),
            strictFormatter("uuuu-MM-dd HHmm"),
            strictFormatter("uuuu-MM-dd HH:mm"),
            strictFormatter("uuuu-MM-dd"),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME
    );

    private TaskDateTime() {
    }

    /**
     * Parses supported input formats with years 0001 through 9999, treating date-only input as midnight.
     * Repeated spaces and tabs between date and time are accepted.
     *
     * @param rawValue The date or date-time text to parse.
     * @return The parsed value, or null for blank or unsupported text.
     */
    public static LocalDateTime parse(String rawValue) {
        String value = rawValue == null ? "" : rawValue.strip().replaceAll("[ \\t]+", " ");
        if (value.isEmpty()) {
            return null;
        }

        for (DateTimeFormatter formatter : INPUT_FORMATTERS) {
            LocalDateTime parsedValue = tryParse(value, formatter);
            if (parsedValue != null) {
                return parsedValue.getYear() >= MIN_YEAR && parsedValue.getYear() <= MAX_YEAR ? parsedValue : null;
            }
        }
        return null;
    }

    /**
     * Checks numeric dates and recognizable AM/PM times while retaining descriptive labels such as Sunday.
     * Bare numbers and punctuation cannot serve as descriptive labels.
     *
     * @param value The nonempty time text to validate.
     * @throws IllegalArgumentException If the value is empty, ambiguous, or contains an invalid date or clock time.
     */
    public static void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("The time cannot be empty.");
        }
        if (parse(value) != null) {
            return;
        }
        String text = value.strip();
        if (NUMERIC_DATE_PREFIX.matcher(text).matches() || text.codePoints().noneMatch(Character::isLetter)) {
            throw new IllegalArgumentException(INVALID_TIME_MESSAGE);
        }
        validateClockTimes(text);
    }

    /**
     * Rejects impossible AM/PM clock times inside descriptive labels without interpreting the label as a date.
     *
     * @param text The descriptive time label.
     * @throws IllegalArgumentException If a recognized clock time has an invalid hour or minute.
     */
    private static void validateClockTimes(String text) {
        Matcher matcher = TWELVE_HOUR_TIME.matcher(text);
        while (matcher.find()) {
            try {
                LocalTime.parse(matcher.group(1).replaceAll("\\s+", ""), CLOCK_FORMATTER);
            } catch (DateTimeParseException exception) {
                throw new IllegalArgumentException(INVALID_TIME_MESSAGE, exception);
            }
        }
    }

    /**
     * Builds a formatter that rejects impossible dates instead of silently adjusting them.
     *
     * @param pattern The supported date pattern.
     * @return The strict, language-independent formatter.
     */
    private static DateTimeFormatter strictFormatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH).withResolverStyle(ResolverStyle.STRICT);
    }

    /**
     * Attempts one format as a date-time, then as a date without a time.
     *
     * @param value The nonblank date text.
     * @param formatter The format to try.
     * @return The parsed value, or null if this format does not match.
     */
    private static LocalDateTime tryParse(String value, DateTimeFormatter formatter) {
        try {
            return LocalDateTime.parse(value, formatter);
        } catch (DateTimeParseException ignored) {
            // The format may describe a date without a time; try that next.
        }
        try {
            return LocalDate.parse(value, formatter).atStartOfDay();
        } catch (DateTimeParseException ignored) {
            // A format mismatch is expected while searching the supported formats.
            return null;
        }
    }

    /**
     * Formats a parsed date-time, retaining the original text when parsing was unavailable.
     *
     * @param dateTime The parsed value, possibly null.
     * @param fallbackText The original text to display for an unparsed value.
     * @return The display text, omitting the time at midnight.
     */
    static String format(LocalDateTime dateTime, String fallbackText) {
        if (dateTime == null) {
            return fallbackText;
        }
        if (dateTime.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return dateTime.toLocalDate().format(DATE_ONLY_DISPLAY_FORMATTER);
        }
        return dateTime.format(DISPLAY_FORMATTER);
    }
}
