package bobo.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for parsing and formatting date and time strings.
 * Supports flexible date-time formats for user inputs and file storage.
 */
public class DateUtil {

    /** Formatter list for date-time inputs with time components. */
    private static final List<DateTimeFormatter> DATE_TIME_FORMATTERS = Arrays.asList(
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm").withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("d/M/uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm").withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm").withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("uuuu/MM/dd HHmm").withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm").withResolverStyle(ResolverStyle.STRICT));

    /** Formatter list for date-only inputs. */
    private static final List<DateTimeFormatter> DATE_FORMATTERS = Arrays.asList(
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("uuuu/MM/dd").withResolverStyle(ResolverStyle.STRICT));

    /** Formatter for displaying LocalDateTime to user. */
    private static final DateTimeFormatter DISPLAY_DATETIME_FORMATTER = DateTimeFormatter
            .ofPattern("MMM d yyyy, h:mma");

    /** Formatter for displaying LocalDate to user. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM d yyyy");

    /** Formatter for storing LocalDateTime to file. */
    private static final DateTimeFormatter STORAGE_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    /** Formatter for storing LocalDate to file. */
    private static final DateTimeFormatter STORAGE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Parses input string as LocalDateTime using supported date-time patterns.
     *
     * @param input Raw date-time string.
     * @return LocalDateTime object if valid format, null otherwise.
     */
    public static LocalDateTime parseDateTime(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        String trimmed = input.trim();
        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                return LocalDateTime.parse(trimmed, formatter);
            } catch (DateTimeParseException e) {
                // Ignore and try next pattern
            }
        }
        return null;
    }

    /**
     * Parses input string as LocalDate using supported date patterns.
     *
     * @param input Raw date string.
     * @return LocalDate object if valid format, null otherwise.
     */
    public static LocalDate parseDate(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        String trimmed = input.trim();
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(trimmed, formatter);
            } catch (DateTimeParseException e) {
                // Ignore and try next pattern
            }
        }
        return null;
    }

    /**
     * Parses input string as either LocalDateTime or LocalDate.
     *
     * @param input Raw date/time string.
     * @return LocalDateTime if time component is present, LocalDate if date only,
     *         or null if unparseable.
     */
    public static Object parseDateTimeOrDate(String input) {
        LocalDateTime dateTime = parseDateTime(input);
        if (dateTime != null) {
            return dateTime;
        }
        LocalDate date = parseDate(input);
        if (date != null) {
            return date;
        }
        return null;
    }

    /**
     * Formats a LocalDate or LocalDateTime object into a user-friendly display
     * string.
     *
     * @param dateTimeOrDate LocalDate, LocalDateTime, or String object.
     * @return User-friendly formatted date/time string.
     */
    public static String formatForDisplay(Object dateTimeOrDate) {
        if (dateTimeOrDate instanceof LocalDateTime) {
            return ((LocalDateTime) dateTimeOrDate).format(DISPLAY_DATETIME_FORMATTER);
        } else if (dateTimeOrDate instanceof LocalDate) {
            return ((LocalDate) dateTimeOrDate).format(DISPLAY_DATE_FORMATTER);
        }
        return String.valueOf(dateTimeOrDate);
    }

    /**
     * Formats a LocalDate or LocalDateTime object into a standard storage string.
     *
     * @param dateTimeOrDate LocalDate, LocalDateTime, or String object.
     * @return Standardized storage date/time string.
     */
    public static String formatForStorage(Object dateTimeOrDate) {
        if (dateTimeOrDate instanceof LocalDateTime) {
            return ((LocalDateTime) dateTimeOrDate).format(STORAGE_DATETIME_FORMATTER);
        } else if (dateTimeOrDate instanceof LocalDate) {
            return ((LocalDate) dateTimeOrDate).format(STORAGE_DATE_FORMATTER);
        }
        return String.valueOf(dateTimeOrDate);
    }
}
