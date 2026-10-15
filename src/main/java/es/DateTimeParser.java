package es;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.List;

/** Parses user-entered dates and times into Java date-time values. */
public final class DateTimeParser {
    private static final List<DateTimeFormatter> FORMATTERS = List.of(
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm"),
            DateTimeFormatter.ofPattern("d/M/uuuu Hm"),
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm"),
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm"),
            DateTimeFormatter.ofPattern("d/M/uuuu"),
            DateTimeFormatter.ofPattern("uuuu-MM-dd"));

    private DateTimeParser() {
    }

    /** Parses a date or date-time in one of the supported formats. */
    public static LocalDateTime parse(String value) throws EsException {
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                TemporalAccessor parsed = formatter.parse(value);
                if (parsed.query(java.time.temporal.TemporalQueries.localTime()) == null) {
                    return LocalDateTime.of(java.time.LocalDate.from(parsed), LocalTime.MIDNIGHT);
                }
                return LocalDateTime.from(parsed);
            } catch (DateTimeParseException ignored) {
                // Try the next accepted format.
            }
        }
        throw new EsException("Invalid date/time '" + value
                + "'. Use yyyy-MM-dd or d/M/yyyy HHmm.");
    }

    /** Formats a date-time for user-facing output. */
    public static String format(LocalDateTime value) {
        if (value.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return value.format(DateTimeFormatter.ofPattern("MMM d yyyy"));
        }
        return value.format(DateTimeFormatter.ofPattern("MMM d yyyy h:mm a"));
    }
}
