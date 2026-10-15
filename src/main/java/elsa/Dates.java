package elsa;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;

import elsa.storage.Storage;

/**
 * Reads and writes the dates that deadlines and events carry.
 *
 * <p>A date has three different forms, and keeping them apart is the point of
 * this class:
 * <ul>
 * <li>what the user types: any of the forms in {@link #INPUT_FORMATS};</li>
 * <li>what the chatbot shows, and what the data file stores: Oct 15 2019 and
 *     2019-10-15 respectively, each written one way only;</li>
 * <li>what the task object holds: a {@link LocalDate}, which knows it is a date
 *     and can therefore be compared with another one.</li>
 * </ul>
 * Several forms are accepted on the way in, but a date is stored in one form
 * only, so a task typed as 15/10/2019 and one typed as 2019-10-15 are the same
 * task once read. Both the chatbot and {@link Storage} read dates through this
 * one class, so a date typed by the user and a date read back from the file
 * cannot disagree.
 */
public class Dates {
    /** The forms a date may be written in, shown when one cannot be read. */
    public static final String ACCEPTED_FORMS =
            "2019-10-15, 15/10/2019 (day/month/year) or Oct 15 2019";

    /**
     * The forms a typed date may take, tried in this order until one fits.
     * All three examples in {@link #ACCEPTED_FORMS} describe the same day, so
     * the day-before-month order of the middle form cannot be mistaken.
     *
     * <p>Day and month are written as a single pattern letter, which accepts
     * both 5 and 05, and the month name is matched without regard to case, so
     * "oct 15 2019" is read as readily as "Oct 15 2019".
     */
    private static final List<DateTimeFormatter> INPUT_FORMATS = List.of(
            inputFormat("uuuu-MM-dd"), // 2019-10-15
            inputFormat("d/M/uuuu"), // 15/10/2019, and 2/12/2019 as 2 December
            inputFormat("MMM d uuuu"), // Oct 15 2019
            inputFormat("MMMM d uuuu")); // October 15 2019

    /**
     * How a date is shown to the user. The pattern letters mean an abbreviated
     * month name, a two digit day, and a four digit year.
     *
     * <p>Locale.ENGLISH is given so that the month is "Oct" on every computer.
     * Without it the name comes out in whatever language the computer is set to,
     * and the same task would read differently on someone else's machine.
     */
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    /**
     * Prevents this class from being instantiated. Every method here is static,
     * so a Dates object would hold nothing and do nothing. Java supplies a public
     * constructor to any class that declares none, so refusing one has to be
     * written down rather than left implied.
     */
    private Dates() {
    }

    /**
     * Builds one of the formats a typed date may take.
     *
     * <p>Two settings matter here. STRICT rejects a day the month does not have,
     * such as 31/2/2019; the default would quietly move it to the 28th, which is
     * worse than refusing it, because the user would never learn their date was
     * changed. STRICT in turn requires the year to be written "uuuu" rather than
     * "yyyy", because "yyyy" means the year within an era and STRICT then insists
     * on being told which era.
     *
     * @param pattern the pattern the date is written in
     * @return a formatter that reads dates written that way
     */
    private static DateTimeFormatter inputFormat(String pattern) {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(pattern)
                .toFormatter(Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT);
    }

    /**
     * Turns text such as "2019-10-15", "15/10/2019" or "Oct 15 2019" into a date.
     * Each accepted form is tried in turn, and a date the calendar does not have,
     * such as 2019-02-30, is refused by all of them.
     *
     * @param text the text to read as a date
     * @return the date that text describes
     * @throws ElsaException if the text is not a date in any accepted form
     */
    public static LocalDate parse(String text) throws ElsaException {
        String trimmed = text.trim();
        for (DateTimeFormatter format : INPUT_FORMATS) {
            try {
                return LocalDate.parse(trimmed, format);
            } catch (DateTimeParseException e) {
                // This form does not fit, so try the next one. Only when none of
                // them fits is the text reported as not being a date at all.
            }
        }
        // Rethrown as an ElsaException so that callers can add their own context
        // and the chatbot reports it like any other problem.
        throw new ElsaException("\"" + text + "\" is not a date. Write it as "
                + ACCEPTED_FORMS);
    }

    /**
     * Returns today's date.
     *
     * <p>Kept here rather than calling LocalDate.now() wherever it is needed, so
     * that there is one place to look when working out which behaviour depends on
     * what day it is. That dependency is why the tests use dates far in the past
     * or far in the future: those stay past and future whenever the tests are run.
     *
     * @return the date on the computer running the chatbot
     */
    public static LocalDate today() {
        return LocalDate.now();
    }

    /**
     * Returns the date as it should appear to the user, for example "Oct 15 2019".
     *
     * @param date the date to show
     * @return the date written in the display format
     */
    public static String format(LocalDate date) {
        return date.format(DISPLAY_FORMAT);
    }

    /**
     * Returns the date as it should be written to the data file, for example
     * "2019-10-15". This is what {@link #parse} reads back, so a task saved and
     * loaded again carries the same date, whichever form it was typed in.
     *
     * @param date the date to store
     * @return the date written in the stored format
     */
    public static String toSaveFormat(LocalDate date) {
        // LocalDate.toString() already produces exactly this form.
        return date.toString();
    }
}
