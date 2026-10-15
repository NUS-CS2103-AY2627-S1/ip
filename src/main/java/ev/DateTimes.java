package ev;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * Reads and writes the dates and times used by tasks.
 *
 * <p>Three different formats meet here and this class is the only place that knows all
 * three: what the user may type, what is shown back to the user, and what is written to
 * the save file. Every formatter is pinned to {@link Locale#ENGLISH} so that the output
 * does not change with the machine the app runs on.
 */
public class DateTimes {

    /** The input formats, listed the way they are shown to the user. */
    public static final String ACCEPTED_FORMATS =
            "2019-12-02, 2019-12-02 1800, 2/12/2019 or 2/12/2019 1800";

    private static final DateTimeFormatter[] DATE_TIME_FORMATS = {
        strict("uuuu-MM-dd HHmm"),
        strict("d/M/uuuu HHmm")
    };

    private static final DateTimeFormatter[] DATE_FORMATS = {
        strict("uuuu-MM-dd"),
        strict("d/M/uuuu")
    };

    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    private static final DateTimeFormatter DISPLAY_DATE_TIME =
            DateTimeFormatter.ofPattern("MMM d yyyy, h:mm a", Locale.ENGLISH);

    /**
     * Returns a formatter that refuses dates the calendar does not have.
     *
     * <p>The default resolver quietly moves an impossible date to the nearest real one, so
     * that 30 February becomes 28 February and the user is never told. Strict resolving
     * rejects it instead, which is why the year is written as {@code uuuu}: strict mode
     * will not resolve a year of era without an era to go with it.
     *
     * @param pattern the pattern to read dates with.
     * @return the formatter, pinned to English so the output cannot follow the machine.
     */
    private static DateTimeFormatter strict(String pattern) {
        return DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT);
    }

    /**
     * Reads a date, and optionally a time, as typed by the user.
     *
     * <p>A date given without a time is taken as the start of that day.
     *
     * @param text what the user typed after {@code /by}, {@code /from}, {@code /to} or {@code on}.
     * @return the date and time it stands for.
     * @throws EvException if the text is not in one of the {@link #ACCEPTED_FORMATS}.
     */
    public static LocalDateTime parse(String text) throws EvException {
        for (DateTimeFormatter format : DATE_TIME_FORMATS) {
            try {
                return LocalDateTime.parse(text, format);
            } catch (DateTimeParseException e) {
                continue;
            }
        }
        for (DateTimeFormatter format : DATE_FORMATS) {
            try {
                return LocalDate.parse(text, format).atStartOfDay();
            } catch (DateTimeParseException e) {
                continue;
            }
        }
        throw new EvException("Not a date: \"" + text + "\"",
                "Use " + ACCEPTED_FORMATS + ".");
    }

    /**
     * Returns a date and time as shown to the user.
     *
     * <p>The time is left out when it is midnight, since that is what a date typed
     * without a time turns into.
     *
     * @param dateTime the moment to show.
     * @return text such as {@code "Dec 2 2019"} or {@code "Dec 2 2019, 6:00 PM"}.
     */
    public static String format(LocalDateTime dateTime) {
        boolean hasTime = !dateTime.toLocalTime().equals(LocalTime.MIDNIGHT);
        return dateTime.format(hasTime ? DISPLAY_DATE_TIME : DISPLAY_DATE);
    }

    /**
     * Returns a date as shown to the user.
     *
     * @param date the date to show.
     * @return text such as {@code "Dec 2 2019"}.
     */
    public static String format(LocalDate date) {
        return date.format(DISPLAY_DATE);
    }

    /**
     * Returns a date and time in the form written to the save file.
     *
     * <p>The save file uses the ISO form rather than the display form so that nothing
     * is lost when the file is read back.
     *
     * @param dateTime the moment to write.
     * @return text such as {@code "2019-12-02T18:00"}.
     */
    public static String toFileFormat(LocalDateTime dateTime) {
        return dateTime.toString();
    }

    /**
     * Reads a date and time back from the save file.
     *
     * @param text one field of a line in the save file.
     * @return the date and time it stands for.
     * @throws EvException if the field is not in the form written by {@link #toFileFormat}.
     */
    public static LocalDateTime fromFileFormat(String text) throws EvException {
        try {
            return LocalDateTime.parse(text);
        } catch (DateTimeParseException e) {
            throw new EvException("Not a saved date: " + text);
        }
    }
}
