package hampster.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * Provides utility methods for parsing and formatting date-time values.
 */
public final class DateTimeParser {

    /**
     * Formatter for user-entered date-time values.
     *
     * <p>Expected format: {@code d/M/uuuu HHmm}</p>
     */
    private static final DateTimeFormatter inputFormat =
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm")
                    .withResolverStyle(ResolverStyle.STRICT);

    /**
     * Formatter for date-time values stored in the save file.
     *
     * <p>Expected format: {@code MMM dd uuuu, hh:mm a}</p>
     */
    private static final DateTimeFormatter outputFormat =
            DateTimeFormatter.ofPattern("MMM dd uuuu, hh:mm a", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT);

    /** Error message shown for invalid date-times entered by a user. */
    private static final String INVALID_INPUT_MESSAGE =
            "That date-time is unworthy of my dossier. Use d/M/uuuu HHmm "
                    + "(e.g. 2/12/2019 1800), minion.";

    /** Error message shown for malformed date-times in the save file. */
    private static final String INVALID_SAVE_MESSAGE =
            "Stored date-time is corrupted; expected MMM dd uuuu, hh:mm a.";

    /**
     * Parses a user-entered date-time string.
     *
     * @param dateTime the date-time in {@code d/M/uuuu HHmm} format
     * @return the parsed date-time
     * @throws DateTimeParseException if the input has an invalid format
     */
    public static LocalDateTime parse(String dateTime) {
        return parseDateTime(dateTime, inputFormat, INVALID_INPUT_MESSAGE);
    }

    /**
     * Parses a date-time loaded from the save file.
     *
     * @param dateTime the date-time in {@code MMM dd uuuu, hh:mm a} format
     * @return the parsed date-time
     * @throws DateTimeParseException if the input has an invalid format
     */
    public static LocalDateTime parseFromSave(String dateTime) {
        return parseDateTime(dateTime, outputFormat, INVALID_SAVE_MESSAGE);
    }

    /** Parses a date-time with the supplied format and standardizes parse errors. */
    private static LocalDateTime parseDateTime(
            String dateTime, DateTimeFormatter format, String errorMessage) {
        if (dateTime == null) {
            throw new DateTimeParseException(errorMessage, "", 0);
        }

        try {
            return LocalDateTime.parse(dateTime, format);

        } catch (DateTimeParseException exception) {
            throw new DateTimeParseException(
                    errorMessage,
                    dateTime,
                    0,
                    exception
            );
        }
    }

    /**
     * Formats a date-time for storage or display.
     *
     * @param dateTime the date-time to format
     * @return the formatted date-time string
     */
    public static String deparse(LocalDateTime dateTime) {
        return dateTime.format(outputFormat);
    }
}
