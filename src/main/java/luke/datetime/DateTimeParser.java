package luke.datetime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;

/**
 * Formats date and time values typed in command flags.
 */
public final class DateTimeParser {
    private static final List<DateTimeFormatter> INPUT_DATES = List.of(
            strictFormatter("d/M/uuuu"),
            strictFormatter("d-M-uuuu"),
            strictFormatter("uuuu-M-d"),
            strictFormatter("uuuu/M/d"),
            strictFormatter("MMM d uuuu"),
            strictFormatter("d MMM uuuu"),
            strictFormatter("d MMMM uuuu"));
    private static final List<DateTimeFormatter> INPUT_DATE_TIMES = List.of(
            strictFormatter("MMM d uuuu h:mm a"),
            strictFormatter("MMM d uuuu H:mm:ss"),
            strictFormatter("d/M/uuuu HHmm"),
            strictFormatter("d/M/uuuu H:mm"),
            strictFormatter("d/M/uuuu H:mm:ss"),
            strictFormatter("d/M/uuuu h:mm a"),
            strictFormatter("d/M/uuuu h:mma"),
            strictFormatter("d/M/uuuu ha"),
            strictFormatter("d/M/uuuu h a"),
            strictFormatter("d-M-uuuu HHmm"),
            strictFormatter("d-M-uuuu H:mm"),
            strictFormatter("d-M-uuuu H:mm:ss"),
            strictFormatter("uuuu-M-d HHmm"),
            strictFormatter("uuuu-M-d H:mm"),
            strictFormatter("uuuu-M-d H:mm:ss"),
            strictFormatter("uuuu/M/d HHmm"),
            strictFormatter("uuuu/M/d H:mm"),
            strictFormatter("uuuu/M/d H:mm:ss"),
            strictFormatter("d MMM uuuu HHmm"),
            strictFormatter("d MMM uuuu H:mm"),
            strictFormatter("d MMM uuuu H:mm:ss"),
            strictFormatter("d MMMM uuuu HHmm"));
    private static final List<DateTimeFormatter> INPUT_TIMES = List.of(
            strictFormatter("HHmm"),
            strictFormatter("H:mm"),
            strictFormatter("H:mm:ss"),
            strictFormatter("h:mm a"),
            strictFormatter("h:mm:ss a"),
            strictFormatter("h:mma"),
            strictFormatter("ha"),
            strictFormatter("h a"));
    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_DATE_TIME =
            DateTimeFormatter.ofPattern("MMM d yyyy h:mm a", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_TIME =
            DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);

    private DateTimeParser() {
    }

    /**
     * Formats a date, date-time, or time typed in a command flag.
     *
     * @param value text from a flag
     * @return formatted date/time text, or the original text if it is not recognized
     */
    public static String formatDateOrTimeFromFlag(String value) {
        String trimmedValue = value.trim();

        String formattedDateTime = formatDateTime(trimmedValue);
        if (formattedDateTime != null) {
            return formattedDateTime;
        }

        String formattedDate = formatDate(trimmedValue);
        if (formattedDate != null) {
            return formattedDate;
        }

        String formattedTime = formatTime(trimmedValue);
        if (formattedTime != null) {
            return formattedTime;
        }

        return value;
    }

    /**
     * Parses a value into a key suitable for sorting by date or time.
     *
     * @param value text from a task flag
     * @return sortable date-time key, or {@code null} if the value is not recognized
     */
    public static LocalDateTime parseSortKey(String value) {
        String trimmedValue = value.trim();

        LocalDateTime parsedDateTime = parseDateTime(trimmedValue);
        if (parsedDateTime != null) {
            return parsedDateTime;
        }

        LocalDate parsedDate = parseDate(trimmedValue);
        if (parsedDate != null) {
            return parsedDate.atStartOfDay();
        }

        LocalTime parsedTime = parseTime(trimmedValue);
        if (parsedTime != null) {
            return LocalDate.MIN.atTime(parsedTime);
        }

        return null;
    }

    /**
     * Formats the value as a date-time if it matches a supported pattern.
     *
     * @param value text to parse
     * @return formatted date-time text, or {@code null} if the value is not recognized
     */
    private static String formatDateTime(String value) {
        LocalDateTime parsedDateTime = parseDateTime(value);
        return parsedDateTime == null ? null : parsedDateTime.format(DISPLAY_DATE_TIME);
    }

    /**
     * Formats the value as a date if it matches a supported pattern.
     *
     * @param value text to parse
     * @return formatted date text, or {@code null} if the value is not recognized
     */
    private static String formatDate(String value) {
        LocalDate parsedDate = parseDate(value);
        return parsedDate == null ? null : parsedDate.format(DISPLAY_DATE);
    }

    /**
     * Formats the value as a time if it matches a supported pattern.
     *
     * @param value text to parse
     * @return formatted time text, or {@code null} if the value is not recognized
     */
    private static String formatTime(String value) {
        LocalTime parsedTime = parseTime(value);
        return parsedTime == null ? null : parsedTime.format(DISPLAY_TIME);
    }

    /**
     * Parses a supported date-time value.
     *
     * @param value text to parse
     * @return parsed date-time, or {@code null} if the value is not recognized
     */
    private static LocalDateTime parseDateTime(String value) {
        for (DateTimeFormatter formatter : INPUT_DATE_TIMES) {
            try {
                return LocalDateTime.parse(value.toUpperCase(), formatter);
            } catch (DateTimeParseException e) {
                // Try the next supported date-time format.
            }
        }
        return null;
    }

    /**
     * Parses a supported date value.
     *
     * @param value text to parse
     * @return parsed date, or {@code null} if the value is not recognized
     */
    private static LocalDate parseDate(String value) {
        for (DateTimeFormatter formatter : INPUT_DATES) {
            try {
                return LocalDate.parse(value, formatter);
            } catch (DateTimeParseException e) {
                // Try the next supported date format.
            }
        }
        return null;
    }

    /**
     * Parses a supported time value.
     *
     * @param value text to parse
     * @return parsed time, or {@code null} if the value is not recognized
     */
    private static LocalTime parseTime(String value) {
        for (DateTimeFormatter formatter : INPUT_TIMES) {
            try {
                return LocalTime.parse(value.toUpperCase(), formatter);
            } catch (DateTimeParseException e) {
                // Try the next supported time format.
            }
        }
        return null;
    }

    /**
     * Creates a formatter for one accepted date/time pattern. The pattern tells
     * the builder what shape to parse, case-insensitive parsing accepts input
     * such as PM or pm, the English locale keeps month and AM/PM text consistent
     * across computers, and strict resolver style rejects invalid dates such as
     * 31/2/2019.
     *
     * @param pattern date/time pattern accepted by {@link DateTimeFormatterBuilder}
     * @return strict formatter for the given pattern
     */
    private static DateTimeFormatter strictFormatter(String pattern) {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(pattern)
                .toFormatter(Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT);
    }
}
