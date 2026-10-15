package kiwi.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.regex.Pattern;

import kiwi.KiwiException;

/**
 * Shared date parsing/formatting for deadlines and events ({@code yyyy-MM-dd} in,
 * {@code MMM dd uuuu} out).
 */
public final class KiwiDate {
    /** How dates are accepted from the user and stored on disk, e.g. {@code 2019-10-15}. */
    public static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    /**
     * How dates are shown in the chatbot UI, e.g. {@code Oct 15 2019}.
     * Uses {@code uuuu} (proleptic year) so year 0000 is not shown as 0001.
     */
    public static final DateTimeFormatter OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);

    /**
     * Strict {@code yyyy-MM-dd} shape: unsigned 4-digit year, 2-digit month, 2-digit day.
     * {@link DateTimeFormatter#ISO_LOCAL_DATE} also accepts signed years such as
     * {@code -0005-03-01} and years outside 0000-9999 such as {@code +10000-01-01}.
     */
    private static final Pattern YYYY_MM_DD_SHAPE = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    private static final String PARSE_ERROR_MESSAGE =
            "Please use a date as yyyy-MM-dd, e.g. 2019-12-02";

    private KiwiDate() {
        // utility class
    }

    /**
     * Parses a {@code yyyy-MM-dd} date string.
     *
     * @param text date text such as {@code 2019-12-02}.
     * @return the parsed date.
     * @throws KiwiException If the text is not an unsigned {@code yyyy-MM-dd} calendar date.
     */
    public static LocalDate parse(String text) throws KiwiException {
        assert text != null : "Parser and Storage always pass a date string";
        String trimmed = text.trim();
        if (!YYYY_MM_DD_SHAPE.matcher(trimmed).matches()) {
            throw new KiwiException(PARSE_ERROR_MESSAGE);
        }
        try {
            return LocalDate.parse(trimmed, INPUT_FORMAT);
        } catch (DateTimeParseException e) {
            throw new KiwiException(trimmed + " is not a real calendar date. "
                    + PARSE_ERROR_MESSAGE);
        }
    }

    /**
     * Formats a date for display, e.g. {@code Oct 15 2019}.
     *
     * @param date date to format.
     * @return formatted text.
     */
    public static String format(LocalDate date) {
        assert date != null : "Formatting is only called with dates stored on tasks";
        return date.format(OUTPUT_FORMAT);
    }
}
