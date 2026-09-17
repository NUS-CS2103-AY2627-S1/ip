package panda.util.datetime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.Temporal;

import panda.exception.parser.IllegalDateTimeException;

/**
 * Converts date and date-time values between input, display, and storage formats.
 */
public final class DateTimeHelper {

    private static final DateTimeFormatter FORMATTER_INPUT = DateTimeFormatter.ofPattern("d/M/uuuu[ H:mm]")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter FORMATTER_OUTPUT = DateTimeFormatter.ofPattern("d MMM yyyy[ H:mm]");
    private static final DateTimeFormatter FORMATTER_STORAGE = DateTimeFormatter.ofPattern("uuuu-MM-dd[ HH:mm]")
            .withResolverStyle(ResolverStyle.STRICT);

    private DateTimeHelper() {
        // Prevents instantiation of utility class.
    }

    /**
     * Parses a user-entered date or date-time.
     *
     * @param dateTime Date or date-time in {@code d/M/uuuu[ H:mm]} format.
     * @return Parsed {@link LocalDate} or {@link LocalDateTime}.
     * @throws IllegalDateTimeException If the value is null, blank, or has an invalid format or value.
     */
    public static Temporal parseInput(String dateTime) throws IllegalDateTimeException {
        return parseUsingFormatter(dateTime, FORMATTER_INPUT);
    }

    /**
     * Parses a stored date or date-time, including the legacy input format.
     *
     * @param dateTime Date or date-time in storage or legacy input format.
     * @return Parsed {@link LocalDate} or {@link LocalDateTime}.
     * @throws IllegalDateTimeException If the value is null, blank, or has an invalid format or value.
     */
    public static Temporal parseStorage(String dateTime) throws IllegalDateTimeException {
        try {
            return parseUsingFormatter(dateTime, FORMATTER_STORAGE);
        } catch (IllegalDateTimeException storageFormatException) {
            return parseInput(dateTime);
        }
    }

    /**
     * Formats a date or date-time for display to the user.
     *
     * @param temporal {@link LocalDate} or {@link LocalDateTime} to format.
     * @return Date or date-time in {@code d MMM yyyy[ H:mm]} format.
     * @throws IllegalArgumentException If the value is null or has an unsupported type.
     */
    public static String formatForDisplay(Temporal temporal) {
        return formatUsingFormatter(temporal, FORMATTER_OUTPUT);
    }

    /**
     * Formats a date or date-time for storage.
     *
     * @param temporal {@link LocalDate} or {@link LocalDateTime} to format.
     * @return Date or date-time in {@code uuuu-MM-dd[ HH:mm]} format.
     * @throws IllegalArgumentException If the value is null or has an unsupported type.
     */
    public static String formatForStorage(Temporal temporal) {
        return formatUsingFormatter(temporal, FORMATTER_STORAGE);
    }

    private static Temporal parseUsingFormatter(String dateTime, DateTimeFormatter formatter)
            throws IllegalDateTimeException {
        if (dateTime == null || dateTime.isBlank()) {
            throw new IllegalDateTimeException(dateTime);
        }

        try {
            return (Temporal) formatter.parseBest(dateTime.strip(), LocalDateTime::from, LocalDate::from);
        } catch (DateTimeParseException exception) {
            throw new IllegalDateTimeException(dateTime, exception);
        }
    }

    private static String formatUsingFormatter(Temporal temporal, DateTimeFormatter formatter) {
        if (temporal == null) {
            throw new IllegalArgumentException("Temporal object cannot be null");
        }
        boolean isSupportedType = temporal instanceof LocalDate || temporal instanceof LocalDateTime;
        if (!isSupportedType) {
            throw new IllegalArgumentException("Unsupported Temporal type: " + temporal.getClass().getName());
        }

        return formatter.format(temporal);
    }
}
