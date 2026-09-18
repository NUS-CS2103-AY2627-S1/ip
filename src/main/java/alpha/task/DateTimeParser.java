package alpha.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;

/** Parses and formats the date and time values used by deadlines and events. */
public final class DateTimeParser {
    private static final List<DateTimeFormatter> DATE_TIME_FORMATTERS = List.of(
            strict("uuuu-MM-dd HHmm"),
            strict("uuuu-MM-dd HH:mm"),
            strict("d/M/uuuu HHmm"),
            strict("d/M/uuuu HH:mm"),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    private static final List<DateTimeFormatter> DATE_FORMATTERS = List.of(
            DateTimeFormatter.ISO_LOCAL_DATE,
            strict("d/M/uuuu"));
    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_TIME =
            DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);

    private DateTimeParser() {
    }

    /** Creates a formatter that rejects invalid calendar dates instead of adjusting them. */
    private static DateTimeFormatter strict(String pattern) {
        return DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT);
    }

    /** Parses an ISO or day-first date/time string into a {@link LocalDateTime}. */
    public static LocalDateTime parse(String value) {
        String trimmedValue = value.trim();
        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                return LocalDateTime.parse(trimmedValue, formatter);
            } catch (DateTimeParseException exception) {
                // Try the next supported format.
            }
        }

        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(trimmedValue, formatter).atStartOfDay();
            } catch (DateTimeParseException exception) {
                // Try the next supported format.
            }
        }

        throw new DateTimeParseException(
                "Expected yyyy-MM-dd or d/M/yyyy HHmm", value, 0);
    }

    /** Formats a date/time for display, omitting midnight for date-only values. */
    public static String format(LocalDateTime value) {
        String date = value.format(DateTimeParser.DISPLAY_DATE);
        if (value.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return date;
        }
        return date + " " + value.format(DateTimeParser.DISPLAY_TIME);
    }
}
