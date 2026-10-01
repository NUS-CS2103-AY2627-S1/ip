package bany.parsers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Parses date-time values entered by the user.
 *
 * <p>At present, Bany accepts only the format {@code dd-MM-yyyy HH:mm}.
 * The formatter uses {@code MM} for the month and {@code mm} for the
 * minutes, while {@code HH} represents a 24-hour clock.</p>
 */
public class DateTimeParser {
    /** Strict formatter used for all supported Bany date-time values. */
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm")
                    .withResolverStyle(ResolverStyle.STRICT);

    /** Creates a date-time parser. */
    public DateTimeParser() {
    }

    /**
     * Checks whether the input is a valid date-time in the supported format.
     *
     * @param input date-time text to validate.
     * @return {@code true} if the input can be parsed as a valid date-time;
     *         {@code false} otherwise.
     */
    public static boolean isValidDateTimeString(String input) {
        if (input == null || input.isBlank()) {
            return false;
        }

        try {
            parseDateTime(input);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Converts a valid date-time string into a {@link LocalDateTime}.
     *
     * @param validInput date-time text in {@code dd-MM-yyyy HH:mm} format.
     * @return the parsed date-time.
     * @throws IllegalArgumentException if the input is null, blank, or invalid.
     */
    public static LocalDateTime createLocalDateTime(String validInput) {
        if (validInput == null || validInput.isBlank()) {
            throw new IllegalArgumentException(
                    "Date-time must use the format dd-MM-yyyy HH:mm: " + validInput);
        }

        try {
            return parseDateTime(validInput);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Date-time must use the format dd-MM-yyyy HH:mm: " + validInput, e);
        }
    }

    /**
     * Parses a date-time and enforces Bany's four-digit year range.
     *
     * @param input date-time text in the supported format.
     * @return parsed date-time.
     * @throws DateTimeParseException if the text or year is invalid.
     */
    private static LocalDateTime parseDateTime(String input) {
        LocalDateTime dateTime = LocalDateTime.parse(input, DATE_TIME_FORMATTER);
        if (dateTime.getYear() < 1 || dateTime.getYear() > 9999) {
            throw new DateTimeParseException(
                    "Year must contain four digits and be between 0001 and 9999.", input, 6);
        }
        return dateTime;
    }

    /**
     * Formats a date-time for display and persistence.
     *
     * @param dateTime date-time to format.
     * @return date-time in {@code dd-MM-yyyy HH:mm} format.
     * @throws IllegalArgumentException if {@code dateTime} is null or its year is unsupported.
     */
    public static String formatLocalDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new IllegalArgumentException("Date-time cannot be null.");
        }
        if (dateTime.getYear() < 1 || dateTime.getYear() > 9999) {
            throw new IllegalArgumentException(
                    "Date-time year must be between 0001 and 9999.");
        }
        return DATE_TIME_FORMATTER.format(dateTime);
    }
}
