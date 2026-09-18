package rem.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Locale;

/**
 * Parses and formats the date and time values used by tasks.
 */
public final class TaskDateTime {
    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_DATE_TIME =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a", Locale.ENGLISH);
    private static final DateTimeFormatter STORAGE_DATE_TIME =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm", Locale.ENGLISH);
    private static final List<DateTimeFormatter> INPUT_FORMATS = List.of(
            new DateTimeFormatterBuilder()
                    .appendPattern("uuuu-MM-dd")
                    .optionalStart()
                    .appendLiteral(' ')
                    .appendPattern("HHmm")
                    .optionalEnd()
                    .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                    .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                    .toFormatter(Locale.ENGLISH).withResolverStyle(ResolverStyle.STRICT),
            new DateTimeFormatterBuilder()
                    .appendPattern("d/M/uuuu")
                    .optionalStart()
                    .appendLiteral(' ')
                    .appendPattern("HHmm")
                    .optionalEnd()
                    .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                    .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                    .toFormatter(Locale.ENGLISH).withResolverStyle(ResolverStyle.STRICT));

    private TaskDateTime() {
    }

    /**
     * Parses an ISO-style date, or a date followed by a 24-hour time without a colon.
     * The slash-separated day/month/year form is also accepted for convenience.
     *
     * @param value User-entered date and optional time.
     * @return Parsed date and time; a date without a time is represented as midnight.
     * @throws DateTimeParseException If the value is not in a supported format.
     */
    public static LocalDateTime parse(String value) {
        for (DateTimeFormatter formatter : INPUT_FORMATS) {
            try {
                return LocalDateTime.parse(value, formatter);
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }
        throw new DateTimeParseException("Unsupported date/time format", value, 0);
    }

    /**
     * Formats a task date for display, including the time only when one was supplied.
     *
     * @param dateTime Date and time to format.
     * @return A readable English date and optional time.
     */
    public static String format(LocalDateTime dateTime) {
        if (dateTime.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return dateTime.format(DISPLAY_DATE);
        }
        return dateTime.format(DISPLAY_DATE_TIME);
    }

    /**
     * Formats a date for display.
     *
     * @param date Date to format.
     * @return A readable English date.
     */
    public static String format(LocalDate date) {
        return date.format(DISPLAY_DATE);
    }

    /**
     * Formats a date and time in Rem's stable, machine-readable storage format.
     *
     * @param dateTime Date and time to store.
     * @return Date and time formatted as {@code yyyy-MM-dd HHmm}.
     */
    public static String formatForStorage(LocalDateTime dateTime) {
        return dateTime.format(STORAGE_DATE_TIME);
    }
}
