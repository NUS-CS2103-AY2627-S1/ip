package soar.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Parses the date and date-time formats accepted by Soar commands.
 */
public final class DateTimeParser {
    /** Accepted formats containing only a date. */
    private static final List<DateTimeFormatter> DATE_FORMATS = List.of(
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT));

    /** Accepted formats containing both a date and a time. */
    private static final List<DateTimeFormatter> DATE_TIME_FORMATS = List.of(
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm").withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm").withResolverStyle(ResolverStyle.STRICT),
            new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("d MMM uuuu h:mm a")
                    .toFormatter(Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME);

    /** Prevents construction of this utility class. */
    private DateTimeParser() {
    }

    /**
     * Parses a value that contains both a date and a time.
     *
     * @param value User-supplied date-time text.
     * @return parsed value, or an empty result when no supported format matches
     */
    public static Optional<LocalDateTime> parseDateTime(String value) {
        for (DateTimeFormatter formatter : DATE_TIME_FORMATS) {
            try {
                return Optional.of(LocalDateTime.parse(value, formatter));
            } catch (DateTimeParseException ignored) {
                // Try the next documented date-time format.
            }
        }
        return Optional.empty();
    }

    /**
     * Parses a value that contains only a date.
     *
     * @param value User-supplied date text.
     * @return parsed value, or an empty result when no supported format matches
     */
    public static Optional<LocalDate> parseDate(String value) {
        for (DateTimeFormatter formatter : DATE_FORMATS) {
            try {
                return Optional.of(LocalDate.parse(value, formatter));
            } catch (DateTimeParseException ignored) {
                // Try the next documented date format.
            }
        }
        return Optional.empty();
    }

    /**
     * Extracts the calendar date from any supported date or date-time value.
     *
     * @param value User-supplied scheduling text.
     * @return calendar date, or an empty result when the text is not recognized
     */
    public static Optional<LocalDate> parseCalendarDate(String value) {
        Optional<LocalDateTime> dateTime = parseDateTime(value);
        if (dateTime.isPresent()) {
            return Optional.of(dateTime.get().toLocalDate());
        }
        return parseDate(value);
    }
}
