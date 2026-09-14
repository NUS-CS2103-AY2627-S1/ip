package penguin.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

import penguin.exception.PenguinException;

/**
 * Provides parsing and formatting utilities for dates and times.
 */
public final class DateTimeUtil {
    /** Formatter for parsing and storing full date/time values. */
    private static final DateTimeFormatter INPUT_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm").withResolverStyle(
                    ResolverStyle.STRICT);

    /** Formatter for displaying date/time values to the user. */
    private static final DateTimeFormatter OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern("d MMM yyyy, h:mma", Locale.ENGLISH);

    /** Formatter for parsing dates used by date-based searches. */
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(
                    ResolverStyle.STRICT);

    /**
     * Prevents instantiation of this utility class.
     */
    private DateTimeUtil() {
    }

    /**
     * Parses a date/time string entered by the user.
     *
     * @param dateTime date/time in yyyy-MM-dd HHmm format.
     * @return parsed date/time.
     * @throws PenguinException if the input is invalid.
     */
    public static LocalDateTime parseDateTime(String dateTime) throws PenguinException {
        if (dateTime == null) {
            throw new PenguinException(
                    "Please enter date and time in yyyy-MM-dd HHmm format.");
        }
        String trimmedInput = dateTime.trim();

        if (!trimmedInput.matches("\\d{4}-\\d{2}-\\d{2} \\d{4}")) {
            throw new PenguinException(
                    "Please enter date and time in yyyy-MM-dd HHmm format.");
        }

        try {
            return LocalDateTime.parse(trimmedInput, INPUT_FORMAT);
        } catch (DateTimeParseException e) {
            throw new PenguinException(
                    "The date or time is out of range.");
        }
    }

    /**
     * Parses a date string entered by the user.
     *
     * @param date date in yyyy-MM-dd format.
     * @return parsed date.
     * @throws PenguinException if the format or date range is invalid.
     */
    public static LocalDate parseDate(String date) throws PenguinException {
        if (date == null) {
            throw new PenguinException(
                    "Please enter a date in yyyy-MM-dd format.");
        }
        String trimmedDate = date.trim();

        if (!trimmedDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new PenguinException(
                    "Please enter a date in yyyy-MM-dd format.");
        }

        try {
            return LocalDate.parse(trimmedDate, DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new PenguinException("The date is out of range.");
        }
    }

    /**
     * Ensures that a date/time is today or later.
     *
     * @param dateTime date/time to validate.
     * @param itemName name of the item being validated.
     * @throws PenguinException if the date is before today.
     */
    public static void validateNotBeforeToday(
            LocalDateTime dateTime, String itemName) throws PenguinException {
        if (dateTime.toLocalDate().isBefore(LocalDate.now())) {
            throw new PenguinException(
                    "The " + itemName + " date cannot be before today.");
        }
    }

    /**
     * Formats a date/time for display.
     *
     * @param dateTime date/time to format.
     * @return formatted date/time.
     */
    public static String formatForDisplay(LocalDateTime dateTime) {
        return dateTime.format(OUTPUT_FORMAT);
    }

    /**
     * Formats a date/time for persistent storage.
     *
     * @param dateTime date/time to format.
     * @return date/time in yyyy-MM-dd HHmm format.
     */
    public static String formatForStorage(LocalDateTime dateTime) {
        return dateTime.format(INPUT_FORMAT);
    }
}
