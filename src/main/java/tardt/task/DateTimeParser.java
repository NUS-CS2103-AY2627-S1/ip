package tardt.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

/** Converts the date and time formats accepted by TardT into {@link LocalDateTime} values. */
final class DateTimeParser {
    /** User-facing explanation of the accepted date and time formats. */
    static final String FORMAT_ERROR = "Invalid date/time format. Use a date such as "
            + "2026-09-29, 29/09/2026, or 29 Sep 2026, optionally followed by a time.";
    private static final List<DateTimeFormatter> DATE_TIME_FORMATS = List.of(
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            formatter("uuuu-MM-dd HH:mm"),
            formatter("d/M/uuuu H:mm"),
            formatter("d MMM uuuu H:mm"),
            formatter("uuuu-MM-dd h:mma"),
            formatter("d/M/uuuu h:mma"),
            formatter("d MMM uuuu h:mma"),
            formatter("uuuu-MM-dd ha"),
            formatter("d/M/uuuu ha"),
            formatter("d MMM uuuu ha"));
    private static final List<DateTimeFormatter> DATE_FORMATS = List.of(
            DateTimeFormatter.ISO_LOCAL_DATE,
            formatter("d/M/uuuu"),
            formatter("d MMM uuuu"));

    private DateTimeParser() {
        // Utility class: no instances are needed.
    }

    /**
     * Parses a supported date, with an optional time. Dates without a time start at midnight.
     *
     * @param input user-entered date and optional time
     * @return the parsed date and time
     * @throws DateTimeParseException if the input does not match a supported format
     */
    static LocalDateTime parse(String input) throws DateTimeParseException {
        // Try date-and-time formats first; otherwise a date-only input would be accepted before its time is examined.
        for (DateTimeFormatter formatter : DATE_TIME_FORMATS) {
            try {
                return LocalDateTime.parse(input, formatter);
            } catch (DateTimeParseException ignored) {
                // Try the next documented format.
            }
        }

        // Date-only input represents the beginning of that date, making deadlines and events deterministic.
        for (DateTimeFormatter formatter : DATE_FORMATS) {
            try {
                return LocalDate.parse(input, formatter).atStartOfDay();
            } catch (DateTimeParseException ignored) {
                // Try the next documented format.
            }
        }
        // Deadline and Event translate this technical parse signal into FORMAT_ERROR for the user interface.
        throw new DateTimeParseException("Unsupported date/time format", input, 0);
    }

    /** Creates a case-insensitive English formatter for a documented input pattern. */
    private static DateTimeFormatter formatter(String pattern) {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(pattern)
                .toFormatter(Locale.ENGLISH);
    }
}
