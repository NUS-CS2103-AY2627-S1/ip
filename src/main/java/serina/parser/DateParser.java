package serina.parser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import serina.exception.SerinaError;
import serina.exception.SerinaException;

/**
 * Parses and formats dates used by Serina tasks.
 */
public final class DateParser {
    private static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern(
            "MMM d yyyy", Locale.ENGLISH);

    private DateParser() {
    }

    /**
     * Parses a date entered by the user.
     *
     * @param dateText Date text in {@code yyyy-MM-dd} format.
     * @return The parsed date.
     * @throws SerinaException If the date is not in the expected format.
     */
    public static LocalDate parseInputDate(String dateText) throws SerinaException {
        return parseDate(dateText, SerinaError.INVALID_DATE);
    }

    /**
     * Parses a date loaded from Serina's save file.
     *
     * @param dateText Saved date text in {@code yyyy-MM-dd} format.
     * @return The parsed date.
     * @throws SerinaException If the date is not in the expected format.
     */
    public static LocalDate parseFileDate(String dateText) throws SerinaException {
        return parseDate(dateText, SerinaError.LOAD_FAILED);
    }

    /**
     * Returns a date in the format shown to the user.
     *
     * @param date Date to format.
     * @return The date formatted as {@code MMM d yyyy}.
     */
    public static String formatDisplayDate(LocalDate date) {
        return date.format(DISPLAY_FORMATTER);
    }

    /**
     * Returns a date in the format saved to disk.
     *
     * @param date Date to format.
     * @return The date formatted as {@code yyyy-MM-dd}.
     */
    public static String formatFileDate(LocalDate date) {
        return date.format(INPUT_FORMATTER);
    }

    /**
     * Parses an ISO date and maps invalid input to the error appropriate for its source.
     *
     * @param dateText Date text in {@code yyyy-MM-dd} format.
     * @param error Error to report if the date cannot be parsed.
     * @return The parsed date.
     * @throws SerinaException If {@code dateText} is not a valid ISO date.
     */
    private static LocalDate parseDate(String dateText, SerinaError error) throws SerinaException {
        assert dateText != null : "Date text should be checked before date parsing.";
        assert error != null : "Date parsing should map failures to a Serina error.";

        try {
            if (!dateText.matches("\\d{4}-\\d{2}-\\d{2}")) {
                throw new DateTimeParseException("Date must use four-digit ISO fields", dateText, 0);
            }
            return LocalDate.parse(dateText, INPUT_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new SerinaException(error);
        }
    }
}
