package auto.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/** Parses and formats task dates consistently. */
public final class DateUtil {
    private static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter
            .ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter STORAGE_FORMATTER = DateTimeFormatter
            .ofPattern("MMM dd uuuu", Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT);

    private DateUtil() {
    }

    /** Parses strict user input in {@code dd/MM/yyyy} format. */
    public static LocalDate parseInput(String value) {
        return parse(value, INPUT_FORMATTER);
    }

    /** Parses the stable {@code MMM dd yyyy} storage representation. */
    public static LocalDate parseStored(String value) {
        return parse(value, STORAGE_FORMATTER);
    }

    /**
     * Parses a date using the supplied strict formatter and exposes all parsing
     * failures through a consistent exception type.
     *
     * @param value date text to parse
     * @param formatter strict formatter defining the required representation
     * @return parsed date
     * @throws IllegalArgumentException if the value is not valid for the formatter
     */
    private static LocalDate parse(String value, DateTimeFormatter formatter) {
        try {
            return LocalDate.parse(value, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("invalid date", e);
        }
    }

    /** Formats a date using {@code MMM dd yyyy}. */
    public static String format(LocalDate date) {
        return date.format(STORAGE_FORMATTER);
    }
}
