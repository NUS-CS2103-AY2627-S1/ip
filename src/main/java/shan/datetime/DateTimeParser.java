package shan.datetime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;

import shan.exception.InvalidArgumentException;

/**
 * Parses and formats the date-times used by Shan's dated tasks.
 */
public final class DateTimeParser {
    private static final DateTimeFormatter DATE_INPUT_FORMATTER = createStrictFormatter("uuuu-MM-dd");
    private static final DateTimeFormatter DATE_DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);
    private static final List<DateTimeFormatter> INPUT_FORMATTERS = List.of(
            createStrictFormatter("uuuu-MM-dd HH:mm"),
            createStrictFormatter("d/M/uuuu HHmm"));
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd uuuu, h:mm a", Locale.ENGLISH);
    private static final DateTimeFormatter STORAGE_FORMATTER = createStrictFormatter("uuuu-MM-dd HH:mm");

    private DateTimeParser() {
    }

    /**
     * Parses a supported date-time string strictly, including calendar validity.
     *
     * @param input Date-time supplied by the user or data file.
     * @return Parsed date-time.
     * @throws InvalidArgumentException If the input has an unsupported format or invalid date.
     */
    public static LocalDateTime parse(String input) throws InvalidArgumentException {
        if (input == null) {
            throw new InvalidArgumentException(
                    "Use a valid date and time in yyyy-MM-dd HH:mm or d/M/yyyy HHmm format.");
        }
        for (DateTimeFormatter formatter : INPUT_FORMATTERS) {
            try {
                return LocalDateTime.parse(input, formatter);
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }
        throw new InvalidArgumentException(
                "Use a valid date and time in yyyy-MM-dd HH:mm or d/M/yyyy HHmm format.");
    }

    /**
     * Parses a calendar date strictly using {@code yyyy-MM-dd}.
     *
     * @param input Date supplied by the user.
     * @return Parsed date.
     * @throws InvalidArgumentException If the input is not a valid date in the supported format.
     */
    public static LocalDate parseDate(String input) throws InvalidArgumentException {
        if (input == null) {
            throw new InvalidArgumentException("Use a valid date in yyyy-MM-dd format.");
        }
        try {
            return LocalDate.parse(input, DATE_INPUT_FORMATTER);
        } catch (DateTimeParseException exception) {
            throw new InvalidArgumentException("Use a valid date in yyyy-MM-dd format.");
        }
    }

    /**
     * Formats a date for chatbot responses.
     *
     * @param date Date to display.
     * @return Human-friendly date.
     */
    public static String formatDateForDisplay(LocalDate date) {
        return date.format(DATE_DISPLAY_FORMATTER);
    }

    /**
     * Formats a date-time for chatbot responses.
     *
     * @param dateTime Date-time to display.
     * @return Human-friendly date-time.
     */
    public static String formatForDisplay(LocalDateTime dateTime) {
        return dateTime.format(DISPLAY_FORMATTER);
    }

    /**
     * Formats a date-time using Shan's canonical save-file representation.
     *
     * @param dateTime Date-time to save.
     * @return Stable date-time representation.
     */
    public static String formatForStorage(LocalDateTime dateTime) {
        return dateTime.format(STORAGE_FORMATTER);
    }

    /**
     * Creates a strict formatter for a supported input or storage pattern.
     *
     * @param pattern Date-time pattern.
     * @return Strict locale-independent formatter.
     */
    private static DateTimeFormatter createStrictFormatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern, Locale.ROOT)
                .withResolverStyle(ResolverStyle.STRICT);
    }
}
