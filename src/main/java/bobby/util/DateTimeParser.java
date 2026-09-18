package bobby.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.Optional;

import bobby.BobbyException;

/**
 * Parses and formats date/time values used by deadline and event tasks.
 */
public class DateTimeParser {
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_DATE_TIME = DateTimeFormatter.ofPattern(
            "MMM d yyyy, h:mma", Locale.ENGLISH);
    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter ISO_DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter SLASH_DATE_TIME = createStrictFormatter("d/M/uuuu HHmm");
    private static final DateTimeFormatter DASH_DATE_TIME = createStrictFormatter("uuuu-MM-dd HHmm");

    /**
     * Parses a user-entered date or date-time into a LocalDateTime.
     *
     * @param input date/time text entered by the user.
     * @return parsed date/time.
     * @throws BobbyException if the input is not in a supported format.
     */
    public static LocalDateTime parse(String input) throws BobbyException {
        String trimmedInput = input.trim().replaceAll("\\s+", " ");
        Optional<LocalDateTime> parsedDateTime = parseDateTime(trimmedInput);
        if (parsedDateTime.isPresent()) {
            return parsedDateTime.get();
        }

        try {
            return LocalDate.parse(trimmedInput, ISO_DATE).atStartOfDay();
        } catch (DateTimeParseException e) {
            throw new BobbyException("Please use a date format like 2019-12-02 or 2/12/2019 1800.");
        }
    }

    /**
     * Formats a date/time for display to the user.
     *
     * @param dateTime date/time to format.
     * @return readable date/time.
     */
    public static String formatForDisplay(LocalDateTime dateTime) {
        if (dateTime.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return dateTime.format(DISPLAY_DATE);
        }
        return dateTime.format(DISPLAY_DATE_TIME).replace("AM", "am").replace("PM", "pm");
    }

    /**
     * Tries each supported date-time pattern that includes both date and time components.
     *
     * @param input trimmed date-time text.
     * @return parsed date-time, or an empty result if none of the supported patterns match.
     */
    private static Optional<LocalDateTime> parseDateTime(String input) {
        DateTimeFormatter[] formatters = {ISO_DATE_TIME, DASH_DATE_TIME, SLASH_DATE_TIME};
        for (DateTimeFormatter formatter : formatters) {
            try {
                return Optional.of(LocalDateTime.parse(input, formatter));
            } catch (DateTimeParseException e) {
                // Try the next supported format.
            }
        }
        return Optional.empty();
    }

    private static DateTimeFormatter createStrictFormatter(String pattern) {
        return new DateTimeFormatterBuilder()
                .appendPattern(pattern)
                .toFormatter(Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT);
    }
}
