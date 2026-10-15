package lumi.datetime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;

import lumi.exception.LumiException;

/**
 * Parses user-entered dates strictly and formats stored dates for display.
 * Keeping this logic in one place gives deadlines and events identical date
 * behavior.
 */
public final class DateTimeParser {
    /** Accepted input formats that include a 24-hour time. */
    private static final List<DateTimeFormatter> DATE_TIME_INPUT_FORMATTERS = List.of(
            strictFormatter("uuuu-MM-dd HHmm"),
            strictFormatter("d/M/uuuu HHmm"));

    /** Accepted input formats that contain only a date. */
    private static final List<DateTimeFormatter> DATE_INPUT_FORMATTERS = List.of(
            strictFormatter("uuuu-MM-dd"),
            strictFormatter("d/M/uuuu"));

    /** Friendly format for dates without a meaningful time component. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    /** Friendly format for dates that include a time. */
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma", Locale.ENGLISH);

    /** Prevents creation of this stateless utility class. */
    private DateTimeParser() {
    }

    /**
     * Parses a date with an optional 24-hour time. A date without a time is
     * represented as midnight at the start of that date.
     *
     * @param value User-entered date and optional time.
     * @return Parsed date and time.
     * @throws LumiException If the value has an unsupported format or invalid date.
     */
    public static LocalDateTime parseUserInput(String value) throws LumiException {
        for (DateTimeFormatter formatter : DATE_TIME_INPUT_FORMATTERS) {
            try {
                return LocalDateTime.parse(value, formatter);
            } catch (DateTimeParseException error) {
                // Try the next supported format.
            }
        }

        for (DateTimeFormatter formatter : DATE_INPUT_FORMATTERS) {
            try {
                return LocalDate.parse(value, formatter).atStartOfDay();
            } catch (DateTimeParseException error) {
                // Try the next supported format.
            }
        }

        throw new LumiException("use a date like 2019-10-15 or 2/12/2019, "
                + "optionally followed by a 24-hour time such as 1800.");
    }

    /**
     * Formats a date for display, omitting the time when it is midnight.
     *
     * @param dateTime Date and time to format.
     * @return Friendly date or date-time text.
     */
    public static String format(LocalDateTime dateTime) {
        if (dateTime.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return dateTime.format(DISPLAY_DATE_FORMATTER);
        }
        return dateTime.format(DISPLAY_DATE_TIME_FORMATTER);
    }

    /** Creates a locale-independent, strict formatter for user input. */
    private static DateTimeFormatter strictFormatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT);
    }
}
