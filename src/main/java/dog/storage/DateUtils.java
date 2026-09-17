package dog.storage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import dog.exceptions.DogException;

/**
 * Utility class for handling date parsing and formatting.
 * Accepts dates in yyyy-MM-dd format and displays in MMM dd yyyy format.
 */
public class DateUtils {
    private static final DateTimeFormatter INPUT_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter OUTPUT_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy");

    /**
     * Parses a date string in yyyy-MM-dd format to LocalDate.
     *
     * @param dateStr the date string to parse.
     * @return the parsed LocalDate.
     * @throws DogException if the date string is invalid.
     */
    public static LocalDate parse(String dateStr) throws DogException {
        try {
            return LocalDate.parse(dateStr, INPUT_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new DogException("ARF ARF That's not a valid date!\n"
                    + "Expected: yyyy-MM-dd (e.g., 2026-09-18)");
        }
    }

    /**
     * Formats a LocalDate to display string in MMM dd yyyy format.
     *
     * @param date the LocalDate to format.
     * @return the formatted date string.
     */
    public static String format(LocalDate date) {
        return date.format(OUTPUT_FORMATTER);
    }
}
