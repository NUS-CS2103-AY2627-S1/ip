package sallman.task;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.Locale;

import sallman.SallmanException;


/**
 * Converts between the date format the user types and the one shown back to
 * them.
 * <p>
 * Dates are entered as {@code yyyy-mm-dd} and displayed as
 * {@code MMM dd yyyy}, e.g. {@code 2019-10-15} is shown as {@code Oct 15 2019}.
 * Keeping both conversions here means the two formats are defined once, rather
 * than once per task type.
 */
public final class TaskDate {

    /** The input format, named here so error messages can quote it. */
    public static final String INPUT_FORMAT = "yyyy-mm-dd";

    /** An example date in the input format, for use in error hints. */
    public static final String EXAMPLE = "2019-10-15";

    /**
     * How a date is shown to the user. The locale is fixed rather than taken
     * from the computer, since the month is otherwise spelled however the
     * computer's language setting spells it: "Sept" on a British or Singapore
     * English machine, "Sept." in German, and in Chinese characters in Chinese.
     */
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    /** Not meant to be instantiated: this class only holds static helpers. */
    private TaskDate() {
    }

    /**
     * Reads a date the user typed.
     *
     * @param text what the user entered where a date was expected
     * @return the date it represents
     * @throws SallmanException if the text is not a date in the input format
     */
    public static LocalDate parse(String text) throws SallmanException {
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            if (text.matches("\\d{4}-\\d{2}-\\d{2}")) {
                // Right shape, so the problem is the calendar, not the format:
                // telling the user to "use yyyy-mm-dd" would not help them.
                throw noSuchDate(text);
            }
            // Translate Java's exception into one that names the format wanted.
            throw new SallmanException("I'm sorry, but I couldn't read \"" + text + "\" as a date.",
                    "Use " + INPUT_FORMAT + ", e.g. " + EXAMPLE + ".");
        }
    }

    /**
     * Returns the error for text in the input format that names no real date,
     * such as {@code 2019-02-30} or {@code 2019-13-01}.
     *
     * @param text a date in the input format that failed to parse
     * @return the exception to throw, saying what is wrong with the date
     */
    private static SallmanException noSuchDate(String text) {
        int year = Integer.parseInt(text.substring(0, 4));
        int month = Integer.parseInt(text.substring(5, 7));
        if (month < 1 || month > 12) {
            return new SallmanException("As a large language model, I must point out there is no month "
                    + month + " in \"" + text + "\".",
                    "Months run from 01 to 12.");
        }
        int days = YearMonth.of(year, month).lengthOfMonth();
        String monthName = Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        return new SallmanException("As a large language model, I must point out there is no such date as "
                + text + ".",
                monthName + " " + year + " has " + days + " days.");
    }

    /**
     * Renders a date for display.
     *
     * @param date the date to show
     * @return the date in {@code MMM dd yyyy} form, e.g. {@code Oct 15 2019}
     */
    public static String format(LocalDate date) {
        return date.format(DISPLAY_FORMAT);
    }
}
