package nova.task;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;

import nova.exception.NovaException;

/**
 * Parses and formats the date/time values used by deadlines and events.
 */
public final class TaskDateTime {
    private static final DateTimeFormatter DATE_TIME_FORMATTER_ISO_DATE = strictFormatter("uuuu-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER_SLASH_DATE = strictFormatter("d/M/uuuu");
    private static final DateTimeFormatter DATE_TIME_FORMATTER_ISO_DATE_TIME =
            strictFormatter("uuuu-MM-dd HHmm");
    private static final DateTimeFormatter DATE_TIME_FORMATTER_SLASH_DATE_TIME =
            strictFormatter("d/M/uuuu HHmm");
    private static final List<DateTimeFormatter> DATE_TIME_FORMATTERS_DATE =
            List.of(DATE_TIME_FORMATTER_ISO_DATE, DATE_TIME_FORMATTER_SLASH_DATE);
    private static final List<DateTimeFormatter> DATE_TIME_FORMATTERS_DATE_TIME =
            List.of(DATE_TIME_FORMATTER_ISO_DATE_TIME, DATE_TIME_FORMATTER_SLASH_DATE_TIME);
    private static final DateTimeFormatter DATE_TIME_FORMATTER_DISPLAY_DATE =
            DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);
    private static final DateTimeFormatter DATE_TIME_FORMATTER_DISPLAY_DATE_TIME =
            DateTimeFormatter.ofPattern("MMM dd uuuu, h:mm a", Locale.ENGLISH);
    private static final DateTimeFormatter DATE_TIME_FORMATTER_STORAGE_DATE_TIME =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm");
    private static final DateTimeFormatter DATE_TIME_FORMATTER_TIME = strictFormatter("HHmm");

    private TaskDateTime() {
        // Utility class; do not instantiate.
    }

    /**
     * Parses a date, with an optional four-digit 24-hour time.
     *
     * @param value user-entered date/time text.
     * @param fieldName name used in error guidance, such as {@code /by}.
     * @return parsed value and whether the user supplied a time.
     * @throws NovaException if the value is not a real date/time in a supported format.
     */
    public static ParsedValue parse(String value, String fieldName) throws NovaException {
        boolean hasTime = value.contains(" ");
        List<DateTimeFormatter> formatters = hasTime
                ? DATE_TIME_FORMATTERS_DATE_TIME
                : DATE_TIME_FORMATTERS_DATE;
        for (DateTimeFormatter formatter : formatters) {
            try {
                LocalDateTime dateTime = hasTime
                        ? LocalDateTime.parse(value, formatter)
                        : LocalDate.parse(value, formatter).atStartOfDay();
                return new ParsedValue(dateTime, hasTime);
            } catch (DateTimeParseException ignored) {
                // Try the other supported date format before reporting an error.
            }
        }
        throw new NovaException("The " + fieldName + " date/time must be a real date in "
                + "yyyy-MM-dd or d/M/yyyy format, optionally followed by HHmm, for example: "
                + "2019-12-02 1800.");
    }

    /**
     * Parses a replacement date/time, allowing a time to reuse the existing date.
     *
     * @param value user-entered replacement value.
     * @param fieldName name used in error guidance, such as {@code /to}.
     * @param existingDateTime current value whose date is retained for a time-only edit.
     * @return parsed replacement and whether it includes a time.
     * @throws NovaException if the replacement is not a supported date or time.
     */
    public static ParsedValue parseForEdit(String value, String fieldName,
            LocalDateTime existingDateTime) throws NovaException {
        assert existingDateTime != null : "Existing date/time must not be null";
        if (!value.matches("\\d{4}")) {
            return parse(value, fieldName);
        }

        try {
            LocalTime time = LocalTime.parse(value, DATE_TIME_FORMATTER_TIME);
            return new ParsedValue(existingDateTime.toLocalDate().atTime(time), true);
        } catch (DateTimeParseException e) {
            throw new NovaException("The " + fieldName
                    + " time must be a real time in HHmm format, for example: 1700.");
        }
    }

    /**
     * Parses a date used by the {@code on} search command.
     *
     * @param value user-entered date text.
     * @return parsed date.
     * @throws NovaException if the value is not a supported date without a time.
     */
    public static LocalDate parseDate(String value) throws NovaException {
        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS_DATE) {
            try {
                return LocalDate.parse(value, formatter);
            } catch (DateTimeParseException ignored) {
                // Try the other supported date format before reporting an error.
            }
        }
        throw new NovaException("The date after on must be a real date in yyyy-MM-dd or d/M/yyyy "
                + "format, for example: on 2019-12-02.");
    }

    /**
     * Formats a task date/time for display.
     *
     * @param dateTime typed date/time value.
     * @param hasTime whether the time was explicitly supplied.
     * @return readable display value.
     */
    public static String formatForDisplay(LocalDateTime dateTime, boolean hasTime) {
        return dateTime.format(hasTime
                ? DATE_TIME_FORMATTER_DISPLAY_DATE_TIME
                : DATE_TIME_FORMATTER_DISPLAY_DATE);
    }

    /**
     * Formats a task date/time in a stable form for storage.
     *
     * @param dateTime typed date/time value.
     * @param hasTime whether a time should be retained.
     * @return canonical storage value.
     */
    public static String formatForStorage(LocalDateTime dateTime, boolean hasTime) {
        return dateTime.format(hasTime
                ? DATE_TIME_FORMATTER_STORAGE_DATE_TIME
                : DATE_TIME_FORMATTER_ISO_DATE);
    }

    /**
     * Creates a formatter that rejects invalid calendar values instead of adjusting them.
     *
     * @param pattern date/time pattern understood by {@link DateTimeFormatterBuilder}
     * @return formatter using strict resolution and the English locale
     */
    private static DateTimeFormatter strictFormatter(String pattern) {
        return new DateTimeFormatterBuilder()
                .appendPattern(pattern)
                .toFormatter(Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT);
    }

    /**
     * Couples a typed date/time with the precision supplied by the user.
     *
     * @param dateTime parsed date/time.
     * @param hasTime whether the original value included a time.
     */
    public record ParsedValue(LocalDateTime dateTime, boolean hasTime) {
        /**
         * Validates and creates a parsed date/time value.
         *
         * @param dateTime parsed date/time
         * @param hasTime whether the original value included a time
         * @throws DateTimeException if {@code dateTime} is {@code null}
         */
        public ParsedValue {
            if (dateTime == null) {
                throw new DateTimeException("dateTime cannot be null");
            }
        }
    }
}
