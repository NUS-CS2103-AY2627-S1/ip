package apollo.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/** Parses the date and date-time formats accepted by Apollo commands. */
public final class DateParser {
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private DateParser() {
    }

    /**
     * Parses a date-time, or combines a date-only value with the supplied default time.
     *
     * @param input date text in {@code d/M/yyyy HHmm} or {@code d/M/yyyy} format
     * @param defaultTime time to use when the input contains only a date
     * @return the parsed date and time
     * @throws DateTimeParseException if the input does not match either accepted format
     */
    public static LocalDateTime parseDateTime(String input, LocalTime defaultTime) {
        assert defaultTime != null
            : "A default time must be supplied for date-only input";
        try {
            return LocalDateTime.parse(input, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException dateTimeException) {
            LocalDate date = LocalDate.parse(input, DATE_FORMATTER);
            return date.atTime(defaultTime);
        }
    }

    /**
     * Parses a date without a time component.
     *
     * @param input date text in {@code d/M/yyyy} format
     * @return the parsed date
     * @throws DateTimeParseException if the input is not a valid date
     */
    public static LocalDate parseDate(String input) {
        return LocalDate.parse(input, DATE_FORMATTER);
    }
}
