package goat.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.Locale;
import java.util.Map;

import goat.GoatException;

/**
 * Turns the date text a user types into a {@link LocalDateTime}, and turns
 * that value back into text for the screen and for the save file.
 *
 * All three conversions live together because they have to agree with each
 * other: whatever {@link #toFileFormat} writes, {@link #parse} must be able
 * to read back. Keeping them in one small class means a task class never
 * has to know a date pattern, and a change of format touches one file.
 *
 * The methods are static because there is nothing for an instance to
 * remember; this class is a collection of conversions, not a thing.
 */
public class DateTimes {
    /**
     * The pattern used when the user includes a time, e.g. "2019-12-02 1800".
     *
     * Locale.ENGLISH is given explicitly so that the month names produced by
     * the display patterns below are the same on every machine. Without it
     * the formatter follows whichever locale the computer is set to, and the
     * program's output would differ from one user to the next.
     */
    private static final DateTimeFormatter INPUT_WITH_TIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm", Locale.ENGLISH);

    /** How a date with no time of day is shown, e.g. "Dec 02 2019". */
    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    /**
     * How a date carrying a time is shown, e.g. "Dec 02 2019, 6:00pm".
     *
     * The plain pattern letter "a" would print "PM", which shouts in the
     * middle of a sentence, and lowercasing the finished string would drag
     * the month name down with it. A builder is used instead so that just
     * the am/pm field is given the wording we want.
     */
    private static final DateTimeFormatter DISPLAY_WITH_TIME =
            new DateTimeFormatterBuilder()
                    .appendPattern("MMM dd yyyy, h:mm")
                    .appendText(ChronoField.AMPM_OF_DAY, Map.of(0L, "am", 1L, "pm"))
                    .toFormatter(Locale.ENGLISH);

    /** What is written to the save file, e.g. "2019-12-02 1800". */
    private static final DateTimeFormatter FILE =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm", Locale.ENGLISH);

    /** Not instantiable: this class is a collection of conversions, not a thing. */
    private DateTimes() {
    }

    /**
     * Reads a date, with an optional time of day after it.
     *
     * Accepted forms are "yyyy-MM-dd" and "yyyy-MM-dd HHmm". A date given on
     * its own is stored at midnight, which is also how it is recognized later
     * as "no time was given" (see {@link #format}). That is a simplification:
     * a task genuinely due at 00:00 will print without a time. Storing a
     * separate boolean, or a second field of type LocalDate, would remove the
     * ambiguity, but neither is worth the extra state for a task list.
     *
     * @param text what the user typed after /by, /from or /to
     * @return the moment that text names
     * @throws GoatException if the text is not one of the accepted forms, or
     *                       names a day that does not exist such as 2019-02-30
     */
    public static LocalDateTime parse(String text) throws GoatException {
        String trimmed = text.trim();
        try {
            // A space can only separate the date from a time, so its presence
            // is what decides which of the two forms this is.
            if (trimmed.contains(" ")) {
                return LocalDateTime.parse(trimmed, INPUT_WITH_TIME);
            }
            return LocalDate.parse(trimmed).atStartOfDay();
        } catch (DateTimeParseException e) {
            // java.time's own message names the character it stopped at, which
            // does not help someone who simply used a different format.
            throw new GoatException("I do not know the date '" + trimmed
                    + "'. Write it as yyyy-MM-dd or yyyy-MM-dd HHmm, "
                    + "e.g. 2019-12-02 or 2019-12-02 1800.");
        }
    }

    /**
     * Renders a date for the user, in a friendlier format than it was typed.
     *
     * @param dateTime the moment to show
     * @return e.g. "Dec 02 2019" or "Dec 02 2019, 6:00pm"
     */
    public static String format(LocalDateTime dateTime) {
        if (dateTime.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return dateTime.format(DISPLAY_DATE);
        }
        return dateTime.format(DISPLAY_WITH_TIME);
    }

    /**
     * Renders a date for the save file.
     *
     * The time is always written, even when it is midnight, so that one
     * pattern reads every saved line. The format is the same one the user
     * types, so a save file edited by hand uses a syntax they already know.
     *
     * @param dateTime the moment to save
     * @return e.g. "2019-12-02 1800"
     */
    public static String toFileFormat(LocalDateTime dateTime) {
        return dateTime.format(FILE);
    }
}
