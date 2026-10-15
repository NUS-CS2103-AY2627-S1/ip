package suki;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * A date, optionally with a time of day, as supplied by the user.
 *
 * <p>This wraps {@link LocalDateTime} rather than using it directly because
 * Suki needs to remember whether the user actually gave a time. "2019-10-15"
 * and "2019-10-15 0000" are the same instant, but only the second one should
 * be shown with a clock time attached.
 *
 * <p>A simpler alternative would be to always store a {@code LocalDateTime} and
 * always print the time; that was rejected because it would show a misleading
 * "12:00AM" for dates the user gave without one.
 */
public class DateTime implements Comparable<DateTime> {
    /** How a date with no time of day is shown, e.g. "Oct 15 2019". */
    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);
    /** How a date with a time is shown, e.g. "Oct 15 2019, 6:00PM". */
    private static final DateTimeFormatter DISPLAY_DATE_TIME =
            DateTimeFormatter.ofPattern("MMM d yyyy, h:mma", Locale.ENGLISH);

    /**
     * Input patterns accepted for a date with a time, tried in order.
     *
     * <p>These resolve strictly so that a date like 2019-02-31 is rejected
     * rather than quietly adjusted to the end of the month, which is what
     * Java's default SMART resolver would do. Strict resolution requires the
     * "uuuu" year field; "yyyy" means era-based year and is not accepted.
     */
    private static final DateTimeFormatter[] DATE_TIME_PATTERNS = {
        strict("uuuu-MM-dd HHmm"),
        strict("uuuu-MM-dd'T'HH:mm"),
        strict("d/M/uuuu HHmm"),
    };

    /** Input patterns accepted for a bare date, tried in order. */
    private static final DateTimeFormatter[] DATE_PATTERNS = {
        strict("uuuu-MM-dd"),
        strict("d/M/uuuu"),
    };

    /** The date, with time set to midnight when the user gave none. */
    private final LocalDateTime value;

    /** Whether the user actually supplied a time of day. */
    private final boolean hasTime;

    /**
     * Creates a DateTime. Private because instances come from {@link #parse}.
     *
     * @param value the date and time
     * @param hasTime whether the time part came from the user
     */
    private DateTime(LocalDateTime value, boolean hasTime) {
        assert value != null : "every parse path supplies a real date before constructing";
        this.value = value;
        this.hasTime = hasTime;
    }

    /**
     * Builds a formatter that refuses out-of-range dates.
     *
     * @param pattern the date pattern, which must use "uuuu" for the year
     * @return a formatter using STRICT resolution
     */
    private static DateTimeFormatter strict(String pattern) {
        return DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT);
    }

    /**
     * Reads a date/time written by the user.
     *
     * <p>Accepted forms are {@code 2019-10-15}, {@code 2019-10-15 1800},
     * {@code 15/10/2019} and {@code 15/10/2019 1800}.
     *
     * @param input the text the user typed
     * @return the parsed date, remembering whether a time was given
     * @throws SukiException if the text matches none of the accepted forms
     */
    public static DateTime parse(String input) throws SukiException {
        String trimmed = input.trim();

        for (DateTimeFormatter pattern : DATE_TIME_PATTERNS) {
            try {
                return new DateTime(LocalDateTime.parse(trimmed, pattern), true);
            } catch (DateTimeParseException e) {
                // Not this pattern; fall through and try the next one.
            }
        }

        for (DateTimeFormatter pattern : DATE_PATTERNS) {
            try {
                return new DateTime(LocalDate.parse(trimmed, pattern).atStartOfDay(), false);
            } catch (DateTimeParseException e) {
                // Not this pattern; fall through and try the next one.
            }
        }

        throw new SukiException("I don't understand the date '" + trimmed + "'. "
                + "Try yyyy-MM-dd or d/M/yyyy, optionally followed by a 24-hour time, "
                + "e.g. 2019-10-15 1800");
    }

    /**
     * Returns this date in a form that {@link #parse} can read back, so that
     * saving and reloading a task does not lose the time of day.
     *
     * @return the date in ISO form, with a time only if one was given
     */
    public String toStorageString() {
        return hasTime
                ? value.format(strict("uuuu-MM-dd'T'HH:mm"))
                : value.toLocalDate().toString();
    }

    /**
     * Orders dates chronologically, so tasks can be sorted or filtered by date.
     *
     * @param other the date to compare against
     * @return a negative number, zero or a positive number as this date is
     *         earlier than, the same as, or later than the other
     */
    @Override
    public int compareTo(DateTime other) {
        return value.compareTo(other.value);
    }

    /**
     * Returns the date as shown to the user.
     *
     * @return the formatted date, including the time only if one was given
     */
    @Override
    public String toString() {
        return hasTime ? value.format(DISPLAY_DATE_TIME) : value.format(DISPLAY_DATE);
    }
}
