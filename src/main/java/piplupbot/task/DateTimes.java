package piplupbot.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

import piplupbot.PiplupBotException;

// ACKNOWLEDGEMENTS: This Java file was written with the help of Claude.

/**
 * Handles how a date and time is written, in each of the three places the
 * program writes one: typed by the user, shown on screen, and stored in the
 * save file.
 *
 * <p>Those three are deliberately different formats. The typed form has to be
 * forgiving, because a person should not have to remember one exact layout; the
 * shown form is written for a person to read; and the stored form is written to
 * be read back by the program, so it is the strict, unambiguous
 * {@code 2019-10-15T18:00} of {@code DateTimeFormatter#ISO_LOCAL_DATE_TIME}.
 * Keeping the stored form separate from the shown form is what lets the wording
 * on screen be changed later without every saved file becoming unreadable.</p>
 *
 * <p>Gathering all three here rather than inside {@link Deadline} and
 * {@link Event} means the two kinds of task cannot drift into accepting or
 * displaying dates differently from each other.</p>
 */
public final class DateTimes {
    /**
     * What the user is told when their date could not be understood.
     * It is a constant so that the wording of the hint and the formats actually
     * accepted below are changed in the same place.
     */
    public static final String FORMAT_HINT =
            "Please write the date as yyyy-MM-dd HHmm or d/M/yyyy HHmm, "
                    + "e.g. 2019-10-15 1800 or 2/12/2019 1800.";

    /**
     * The layouts accepted from the user when a time of day is given, tried in
     * this order. The ISO form comes first because that is what the save file
     * holds, so loading takes the first branch and never has to fail its way
     * through the others.
     */
    private static final DateTimeFormatter[] DATE_TIME_FORMATS = {
        DateTimeFormatter.ISO_LOCAL_DATE_TIME, // 2019-10-15T18:00, as stored in the file
        createStrictFormatter("uuuu-MM-dd HHmm"), // 2019-10-15 1800
        createStrictFormatter("uuuu-MM-dd HH:mm"), // 2019-10-15 18:00
        createStrictFormatter("d/M/uuuu HHmm"), // 2/12/2019 1800
        createStrictFormatter("d/M/uuuu HH:mm"), // 2/12/2019 18:00
    };

    /**
     * The layouts accepted when only a day is given. A deadline is often a whole
     * day rather than a moment, so these are taken to mean midnight at the start
     * of that day -- stated here because the user is never shown that choice
     * being made for them.
     */
    private static final DateTimeFormatter[] DATE_FORMATS = {
        DateTimeFormatter.ISO_LOCAL_DATE, // 2019-10-15
        createStrictFormatter("d/M/uuuu"), // 2/12/2019
    };

    /**
     * How a date is shown on screen, e.g. {@code Oct 15 2019 06:00 PM}.
     * {@code Locale.ENGLISH} is fixed rather than left to the machine's own
     * locale, so the bot's replies -- and the test cases that record them --
     * read the same on every machine.
     */
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d uuuu hh:mm a", Locale.ENGLISH);

    /** Prevents instances being created; every member of this class is static. */
    private DateTimes() {
    }

    /**
     * Returns a formatter that rejects a date that does not exist, such as
     * {@code 31/2/2019}.
     *
     * <p>By default a formatter is lenient enough to turn that into 28 February,
     * which would store a date the user never typed. {@link ResolverStyle#STRICT}
     * refuses it instead -- and it is the reason the patterns above use
     * {@code uuuu} rather than {@code yyyy}: {@code yyyy} means "year of era",
     * which is ambiguous without an era (AD or BC) to go with it, so a strict
     * formatter would reject every date given with it.</p>
     *
     * @param pattern The layout, in {@code DateTimeFormatter} pattern letters.
     * @return A formatter for that layout that accepts only real dates.
     */
    private static DateTimeFormatter createStrictFormatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT);
    }

    /**
     * Returns the date and time named by what the user typed -- or by what the
     * save file holds.
     *
     * <p>Each accepted layout is tried in turn, because a
     * {@code DateTimeFormatter} can only answer "does this text match me?" by
     * throwing. Failing to match is therefore expected here and is not an error:
     * only running out of layouts to try is.</p>
     *
     * <p>Every layout has exactly one space between the day and the time, while
     * a person may well type two. So each run of spaces is turned into a single
     * one before the layouts are tried, and {@code 2019-10-15  1800} is read
     * like {@code 2019-10-15 1800}. What the user typed is still what an error
     * quotes back, so that they can recognize it.</p>
     *
     * @param text The date as written, with or without surrounding spaces.
     * @return The date and time it names.
     * @throws PiplupBotException If no accepted layout matches, so that the
     *                            caller can show the user the hint rather than
     *                            ending the conversation with a stack trace.
     */
    public static LocalDateTime parse(String text) throws PiplupBotException {
        String trimmed = text.trim();
        String singleSpaced = trimmed.replaceAll("\\s+", " ");

        for (DateTimeFormatter format : DATE_TIME_FORMATS) {
            try {
                return LocalDateTime.parse(singleSpaced, format);
            } catch (DateTimeParseException e) {
                // Not this layout; try the next one.
            }
        }

        for (DateTimeFormatter format : DATE_FORMATS) {
            try {
                return LocalDate.parse(singleSpaced, format).atStartOfDay();
            } catch (DateTimeParseException e) {
                // Not this layout either.
            }
        }

        throw new PiplupBotException("Pip... I don't understand the date \"" + trimmed + "\".",
                FORMAT_HINT);
    }

    /**
     * Returns a date written the way the task list shows it,
     * e.g. {@code Oct 15 2019 06:00 PM}.
     *
     * @param dateTime The date to show.
     * @return The date written for a person to read.
     */
    public static String format(LocalDateTime dateTime) {
        return dateTime.format(DISPLAY_FORMAT);
    }

    /**
     * Returns a date written the way the save file holds it,
     * e.g. {@code 2019-10-15T18:00}.
     * {@link #parse} accepts this form -- it is the first layout it tries --
     * which is what makes saving and loading the exact reverse of each other.
     *
     * <p>{@code LocalDateTime.toString()} is the ISO form already, and is used
     * in preference to formatting with {@code ISO_LOCAL_DATE_TIME} because it
     * leaves out seconds that are zero, which every date this bot stores has.</p>
     *
     * @param dateTime The date to store.
     * @return The date in ISO form.
     */
    public static String toFileString(LocalDateTime dateTime) {
        return dateTime.toString();
    }
}
