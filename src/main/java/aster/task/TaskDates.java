package aster.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Converts task dates between the form the user types, the form shown back to the
 * user, and the form written to the data file.
 *
 * <p>A date is typed as {@code yyyy-MM-dd} and shown as {@code MMM dd yyyy}, so what
 * Aster prints is never mistaken for what to type. The months are named in English
 * whatever the computer is set to, and the stored form is numeric, so neither a saved
 * file nor a printed date changes meaning on a differently configured computer.
 *
 * <p>The typed form and the stored form are deliberately the same one. Should they
 * ever need to differ, this class is the only place that has to change.
 */
public final class TaskDates {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    /**
     * Prevents instances being created, since this class holds only static helpers.
     */
    private TaskDates() {
    }

    /**
     * Returns the date the given text holds, or {@code null} if it does not hold one.
     *
     * <p>Returning {@code null} rather than throwing lets each caller explain the
     * problem in its own words, because a mistyped command and an unreadable saved
     * file need to be reported differently.
     *
     * <p>The reading is strict, so a date that never existed is refused rather than
     * quietly moved to a nearby one: {@code 2019-02-29} is refused because 2019 is not
     * a leap year, while {@code 2020-02-29} is accepted.
     *
     * @param text the text to read, which may be {@code null} or blank.
     * @return the date the text holds, or {@code null} if it is not a date written in
     *     the accepted form.
     */
    public static LocalDate parseOrNull(String text) {
        if (text == null) {
            return null;
        }
        try {
            return LocalDate.parse(text.trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Returns the date as it is shown to the user.
     *
     * @param date the date to show.
     * @return the date written as {@code MMM dd yyyy}, for example {@code Dec 02 2019}.
     */
    static String format(LocalDate date) {
        return date.format(DISPLAY_FORMAT);
    }

    /**
     * Returns the date as it is written to the data file.
     *
     * @param date the date to store.
     * @return the date in the ISO form {@code yyyy-MM-dd}, for example
     *     {@code 2019-12-02}.
     */
    public static String toStorage(LocalDate date) {
        return date.toString();
    }
}
