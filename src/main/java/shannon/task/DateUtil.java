package shannon.task;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import shannon.exception.InvalidDateException;

/**
 * The date and time formats that tasks are typed in, saved in and shown in, and the reading of
 * them.
 * <p>
 * {@link Deadline} and {@link EventTime} both use these, so a date means the same thing, and is
 * checked the same way, whichever command it was typed into. Package-private, because nothing
 * outside {@code shannon.task} needs to know how the formats are spelled.
 */
final class DateUtil {

    /**
     * The date format typed and saved, e.g. {@code 2026-08-09}.
     * {@code ISO_LOCAL_DATE} is strict, so a date that does not exist, such as February 30, is
     * refused rather than quietly rolled over into March.
     */
    static final DateTimeFormatter DATE_INPUT_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    /**
     * The date format shown to the user, e.g. {@code Aug 09 2026}.
     * {@link Locale#ENGLISH} is fixed explicitly so the month name does not change with whatever
     * locale the machine happens to be set to.
     */
    static final DateTimeFormatter DATE_DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    /**
     * The time format typed, saved and shown, in 24-hour form, e.g. {@code 14:00} or {@code 9:30}.
     * Strict, so that {@code 24:00} is refused instead of being read as the next midnight.
     */
    static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("H:mm").withResolverStyle(ResolverStyle.STRICT);

    /** The format for naming a month in an error message, e.g. {@code February 2026}. */
    private static final DateTimeFormatter MONTH_FORMAT =
            DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);

    /**
     * The shape of a typed date, with the year, month and day captured as groups 1 to 3.
     * Checking the shape apart from the calendar is what tells "not a date at all" apart from
     * "the right form, but no such day".
     */
    private static final Pattern DATE_SHAPE = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");

    /** Not meant to be instantiated: this class is only a home for the shared formats. */
    private DateUtil() {
    }

    /**
     * Turns text in {@code yyyy-mm-dd} form into a date.
     *
     * @param text the date, with no spaces around it.
     * @return the date it names.
     * @throws InvalidDateException if the text is not in that form, or names a day that does not
     *                              exist, such as {@code 2026-02-30}; the message says which.
     */
    static LocalDate parseDate(String text) throws InvalidDateException {
        Matcher matcher = DATE_SHAPE.matcher(text);
        if (!matcher.matches()) {
            throw InvalidDateException.ofUnreadableDate(text);
        }
        try {
            return LocalDate.parse(text, DATE_INPUT_FORMAT);
        } catch (DateTimeParseException e) {
            // The user did follow the format, so repeating it would not help: say what is wrong
            // with the date itself instead.
            int year = Integer.parseInt(matcher.group(1));
            int month = Integer.parseInt(matcher.group(2));
            int day = Integer.parseInt(matcher.group(3));
            throw InvalidDateException.ofNonExistentDate(text, explainNonExistentDate(year, month, day));
        }
    }

    /**
     * Returns whether text is in {@code yyyy-mm-dd} form, whether or not the day it names exists.
     *
     * @param text the text to check, with no spaces around it.
     * @return {@code true} if it has the shape of a date.
     */
    static boolean hasDateShape(String text) {
        return DATE_SHAPE.matcher(text).matches();
    }

    /**
     * Turns text in 24-hour {@code H:mm} form, e.g. {@code 14:00}, into a time of day.
     *
     * @param text the time, with no spaces around it.
     * @return the time it names.
     * @throws InvalidDateException if the text is not a time in that form, e.g. {@code 2pm} or
     *                              {@code 25:00}.
     */
    static LocalTime parseTime(String text) throws InvalidDateException {
        try {
            return LocalTime.parse(text, TIME_FORMAT);
        } catch (DateTimeParseException e) {
            throw InvalidDateException.ofUnreadableTime(text);
        }
    }

    /**
     * Returns why a date typed in the right form does not exist, for the error message.
     *
     * @param year  the year as typed.
     * @param month the month as typed, which may be out of range.
     * @param day   the day as typed, which may be out of range.
     * @return the reason, e.g. {@code February 2026 only has 28 days}.
     */
    private static String explainNonExistentDate(int year, int month, int day) {
        if (month < 1 || month > 12) {
            return "there is no month " + month;
        }
        if (day < 1) {
            return "days are counted from 01";
        }
        YearMonth yearMonth = YearMonth.of(year, month);
        return yearMonth.format(MONTH_FORMAT) + " only has " + yearMonth.lengthOfMonth() + " days";
    }
}
