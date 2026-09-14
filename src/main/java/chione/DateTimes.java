package chione;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * Turns the dates the user types into {@link LocalDateTime} values and back again.
 *
 * <p>Chione reads dates in three different shapes: what the user types, what is
 * shown back to them, and what is written to the save file. Keeping all three in
 * one place means the task classes never have to know a date format, and a
 * change to any one shape happens here rather than in several files at once.
 *
 * <p>This class only holds static helpers, so it is {@code final} and its
 * constructor is private: there is never any reason to create one.
 */
public final class DateTimes {
    /**
     * The format used both for input that includes a time and for the save file,
     * e.g. {@code "2019-10-15 1800"}.
     *
     * <p>{@link Locale#ENGLISH} is fixed rather than left to the machine's own
     * locale, so the program behaves the same way on every computer.
     *
     * <p>The formatter is made {@link ResolverStyle#STRICT}. Left at its default,
     * it would quietly turn {@code "2019-02-30 1800"} into February 28th rather
     * than refuse it, and a task would end up on a day the user never typed.
     * Strict resolution needs the year written as {@code uuuu} (the proleptic
     * year) rather than {@code yyyy} (the year of an era, which is ambiguous
     * without an era to go with it); for any year the user will type, the two
     * read and print identically.
     */
    private static final DateTimeFormatter INPUT_WITH_TIME =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT);

    /** How a date with no time of day is shown, e.g. {@code "Oct 15 2019"}. */
    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    /**
     * How the time of day is shown, e.g. {@code "6:00PM"}.
     *
     * <p>Formatted separately from the date so that only this half is lower-cased
     * for display: lower-casing the whole thing would turn {@code "Oct"} into
     * {@code "oct"} as well.
     */
    private static final DateTimeFormatter DISPLAY_TIME =
            DateTimeFormatter.ofPattern("h:mma", Locale.ENGLISH);

    /** Prevents this utility class from being instantiated. */
    private DateTimes() {
    }

    /**
     * Reads a date, with or without a time of day.
     *
     * <p>Both {@code "2019-10-15"} and {@code "2019-10-15 1800"} are accepted.
     * When no time is given the moment is taken to be midnight, which is also how
     * {@link #format} decides whether to show a time at all. The consequence is
     * that a task due at exactly midnight is displayed as a plain date; carrying a
     * separate "was a time given?" flag would remove that corner case, but it is
     * not worth the extra field for a task list.
     *
     * @param text what the user typed after {@code /by}, {@code /from} or {@code /to}
     * @return the moment it refers to
     * @throws ChioneException if the text is not a date Chione can read
     */
    public static LocalDateTime parse(String text) throws ChioneException {
        try {
            return LocalDateTime.parse(text, INPUT_WITH_TIME);
        } catch (DateTimeParseException e) {
            // Not a date-and-time; fall through and try a plain date instead.
        }

        try {
            // LocalDate.parse expects the ISO shape yyyy-MM-dd by default, and
            // is already strict: a day that does not exist is refused here too.
            return LocalDate.parse(text).atStartOfDay();
        } catch (DateTimeParseException e) {
            throw new ChioneException("I can't read the date \"" + text + "\". "
                    + "Write it as 2019-10-15, or 2019-10-15 1800 to include a time.");
        }
    }

    /**
     * Reads a plain date with no time of day, as used by the {@code on} command.
     *
     * @param text what the user typed, e.g. {@code "2019-10-15"}
     * @return the date it refers to
     * @throws ChioneException if the text is not a date Chione can read
     */
    public static LocalDate parseDate(String text) throws ChioneException {
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            throw new ChioneException("I can't read the date \"" + text + "\". "
                    + "Write it as 2019-10-15.");
        }
    }

    /**
     * Renders a moment for the user, showing the time only when there is one.
     *
     * @param moment the moment to show
     * @return e.g. {@code "Oct 15 2019"} or {@code "Oct 15 2019, 6:00pm"}
     */
    public static String format(LocalDateTime moment) {
        if (moment.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return moment.format(DISPLAY_DATE);
        }
        // "6:00PM" is lower-cased to "6:00pm", which sits better in a sentence.
        return moment.format(DISPLAY_DATE) + ", "
                + moment.format(DISPLAY_TIME).toLowerCase(Locale.ENGLISH);
    }

    /**
     * Renders a plain date for the user.
     *
     * @param date the date to show
     * @return e.g. {@code "Oct 15 2019"}
     */
    public static String formatDate(LocalDate date) {
        return date.format(DISPLAY_DATE);
    }

    /**
     * Renders a moment for the save file.
     *
     * <p>The same format is used for input, so the file stays easy for a human to
     * read and edit, and anything valid in the file is also valid to type.
     *
     * @param moment the moment to store
     * @return e.g. {@code "2019-10-15 1800"}
     */
    public static String toSaveFormat(LocalDateTime moment) {
        return moment.format(INPUT_WITH_TIME);
    }
}
