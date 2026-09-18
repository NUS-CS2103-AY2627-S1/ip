package alfred;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Converts between a {@link LocalDate} and the two forms a date takes as text:
 * {@code yyyy-mm-dd} when typed or saved, {@code MMM dd yyyy} when shown.
 */
public class Dates {
    private static final String DATE_INPUT_FORMAT = "yyyy-mm-dd";

    private static final String DATE_INPUT_EXAMPLE = "2019-10-15";

    /** Matches the shape of a date, not whether the day exists: {@code 2019-02-30} matches. */
    private static final Pattern DATE_INPUT_SHAPE = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");

    /** Pinned to English so the month name does not change with the computer's locale. */
    private static final DateTimeFormatter DATE_DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private Dates() {
    }

    /**
     * Writes a date the way it is shown to the user.
     *
     * @param date the day to write.
     * @return the date in the reader's form, such as {@code Oct 15 2019}.
     */
    public static String format(LocalDate date) {
        return DATE_DISPLAY_FORMAT.format(date);
    }

    /**
     * Reads a date written as {@code yyyy-mm-dd}.
     *
     * @param text the date as it was written, already trimmed.
     * @return the day that text names.
     * @throws AlfredException if the text is not in that format, or names a
     *         day that does not exist.
     */
    static LocalDate parse(String text) throws AlfredException {
        if (!DATE_INPUT_SHAPE.matcher(text).matches()) {
            throw new AlfredException("I don't know '" + text + "' as a date, sir. Do use "
                    + DATE_INPUT_FORMAT + ", as in " + DATE_INPUT_EXAMPLE + ".");
        }
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            throw new AlfredException("There is no such date as '" + text
                    + "', sir. Do check the day and the month.");
        }
    }
}
