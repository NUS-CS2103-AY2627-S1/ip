package bingusdingus.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

/** Parses and formats the date and time values used by deadline and event tasks. */
public final class DateTimeParser {
    private static final Locale DISPLAY_LOCALE = Locale.ENGLISH;
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu", DISPLAY_LOCALE);
    private static final DateTimeFormatter DISPLAY_TIME_FORMAT =
            DateTimeFormatter.ofPattern("h:mm a", DISPLAY_LOCALE);
    private static final List<DateTimeFormatter> SUPPORTED_DATE_TIME_FORMATS = List.of(
            strictFormatter("d/M/uuuu HHmm"),
            strictFormatter("uuuu-MM-dd HHmm"),
            strictFormatter("d/M/uuuu HH:mm"),
            strictFormatter("uuuu-MM-dd HH:mm"));
    private static final List<DateTimeFormatter> SUPPORTED_DATE_FORMATS = List.of(
            strictFormatter("d/M/uuuu"),
            strictFormatter("uuuu-MM-dd"));

    private DateTimeParser() {
        // Utility class; do not instantiate.
    }

    /**
     * Parses a user-entered date or date/time.
     *
     * <p>Date-only values are represented at midnight so that one typed value can
     * support both deadlines and event start/end times.</p>
     *
     * @param text the date or date/time entered by the user.
     * @return the parsed local date/time.
     * @throws DateTimeParseException if the value is not in a supported format.
     */
    public static LocalDateTime parse(String text) {
        if (text == null || text.isBlank() || !text.equals(text.trim())) {
            throw new DateTimeParseException("Date/time must not be blank or padded", text == null ? "" : text, 0);
        }

        LocalDateTime parsedValue = parseWithFormats(text, SUPPORTED_DATE_TIME_FORMATS,
                formatter -> LocalDateTime.parse(text, formatter));
        if (parsedValue != null) {
            return parsedValue;
        }

        parsedValue = parseWithFormats(text, SUPPORTED_DATE_FORMATS,
                formatter -> LocalDate.parse(text, formatter).atStartOfDay());
        if (parsedValue != null) {
            // Date-only input is normalized to midnight before it reaches a task.
            assert parsedValue.toLocalTime().equals(LocalTime.MIDNIGHT);
            return parsedValue;
        }

        throw new DateTimeParseException("Unsupported date/time format", text, 0);
    }

    /** Parses the ISO date/time representation written by task storage. */
    public static LocalDateTime parseStorage(String text) {
        if (text == null || text.isBlank()) {
            throw new DateTimeParseException("Stored date/time must not be blank", text == null ? "" : text, 0);
        }
        try {
            return LocalDateTime.parse(text);
        } catch (DateTimeParseException e) {
            throw new DateTimeParseException("Invalid stored date/time", text, 0, e);
        }
    }

    /** Returns a human-readable date, including a time when it is not midnight. */
    public static String format(LocalDateTime value) {
        String date = value.format(DISPLAY_DATE_FORMAT);
        return value.toLocalTime().equals(LocalTime.MIDNIGHT)
                ? date
                : date + " " + value.format(DISPLAY_TIME_FORMAT);
    }

    /** Returns a stable ISO value suitable for the task storage file. */
    public static String formatForStorage(LocalDateTime value) {
        return value.toString();
    }

    /** Creates a strict formatter so invalid calendar dates are rejected. */
    private static DateTimeFormatter strictFormatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern, DISPLAY_LOCALE)
                .withResolverStyle(ResolverStyle.STRICT);
    }

    /** Returns the first successfully parsed value, or null when all formats fail. */
    private static LocalDateTime parseWithFormats(String text,
                                                  List<DateTimeFormatter> formatters,
                                                  Function<DateTimeFormatter, LocalDateTime> parser) {
        for (DateTimeFormatter formatter : formatters) {
            try {
                return parser.apply(formatter);
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }
        return null;
    }
}
