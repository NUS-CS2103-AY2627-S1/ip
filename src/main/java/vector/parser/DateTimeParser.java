package vector.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

import vector.VectorException;

/**
 * Parses and formats dates and times.
 */
public class DateTimeParser {
    private static final List<DateTimeFormatter> FORMATTERS = Arrays.asList(
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm").withResolverStyle(java.time.format.ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm").withResolverStyle(java.time.format.ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(java.time.format.ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(java.time.format.ResolverStyle.STRICT)
    );

    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern(
            "MMM d yyyy, h:mm a", java.util.Locale.ENGLISH);
    private static final DateTimeFormatter FILE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    /**
     * Parses a string into a LocalDateTime.
     * Tries multiple formats. If time is not provided, defaults to 00:00.
     *
     * @param text The string to parse.
     * @return The parsed LocalDateTime.
     * @throws VectorException if the string does not match any known format.
     */
    public static LocalDateTime parse(String text) throws VectorException {
        return FORMATTERS.stream()
                .map(formatter -> {
                    try {
                        return LocalDateTime.parse(text, formatter);
                    } catch (DateTimeParseException e) {
                        try {
                            return LocalDate.parse(text, formatter).atStartOfDay();
                        } catch (DateTimeParseException ex) {
                            return null;
                        }
                    }
                })
                .filter(java.util.Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new VectorException(
                        "Invalid date/time format. Please use 'yyyy-MM-dd' or 'd/M/yyyy HHmm'."));
    }

    /**
     * Parses a string into a LocalDate.
     * Tries multiple formats.
     *
     * @param text The string to parse.
     * @return The parsed LocalDate.
     * @throws VectorException if the string does not match any known format.
     */
    public static LocalDate parseDate(String text) throws VectorException {
        return FORMATTERS.stream()
                .map(formatter -> {
                    try {
                        return LocalDate.parse(text, formatter);
                    } catch (DateTimeParseException ex) {
                        return null;
                    }
                })
                .filter(java.util.Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new VectorException("Invalid date format. Please use 'yyyy-MM-dd'."));
    }

    /**
     * Formats a LocalDateTime for displaying to the user.
     *
     * @param dt The LocalDateTime to format.
     * @return The formatted string.
     */
    public static String format(LocalDateTime dt) {
        return dt.format(DISPLAY_FORMAT);
    }

    /**
     * Formats a LocalDateTime for saving to the data file.
     *
     * @param dt The LocalDateTime to format.
     * @return The formatted string.
     */
    public static String formatForFile(LocalDateTime dt) {
        return dt.format(FILE_FORMAT);
    }
}
