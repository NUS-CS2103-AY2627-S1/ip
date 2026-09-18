package meowmeow.task;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

import meowmeow.MeowmeowException;

/**
 * A date, optionally with a time-of-day, as understood from something the
 * user typed after {@code /by}, {@code /from} or {@code /to}.
 *
 * <p>This is a small wrapper around {@link LocalDate} (plus an optional
 * {@link LocalTime}) rather than a bare {@code LocalDate} or
 * {@code LocalDateTime}, because the user is allowed to give just a date
 * ({@code 2019-10-15}) <em>or</em> a date and time ({@code 2/12/2019 1800}).
 * A bare {@code LocalDate} would throw the time away; a bare
 * {@code LocalDateTime} would invent a midnight that the user never typed and
 * then print it back at them. Keeping {@code time} nullable lets us render the
 * two cases differently.
 *
 * <p>Instances are immutable and are created only through {@link #parse}.
 */
public class TaskDateTime {

    /** One accepted input pattern, and whether it carries a time-of-day. */
    private record InputFormat(DateTimeFormatter formatter, boolean hasTime) { }

    /** The one-line reminder of accepted formats, reused in every parse error. */
    public static final String FORMAT_HINT =
            " Try: 2/12/2019 1800, 2/12/2019, 2019-12-02 1800, or 2019-12-02.";

    // Accepted input patterns, tried in this order. The date-and-time ones
    // come first so "2/12/2019 1800" isn't half-matched by a date-only
    // pattern. "uuuu" (not "yyyy") plus ResolverStyle.STRICT makes Java
    // actually reject an impossible date like 30/2/2019 instead of quietly
    // snapping it to the 28th.
    private static final InputFormat[] INPUT_FORMATS = {
        new InputFormat(strict("d/M/uuuu HHmm"), true),
        new InputFormat(strict("uuuu-M-d HHmm"), true),
        new InputFormat(strict("d/M/uuuu"), false),
        new InputFormat(strict("uuuu-M-d"), false),
    };

    /** How a date is shown on screen, e.g. {@code "Oct 15 2019"}. */
    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("MMM d uuuu", Locale.ENGLISH);
    /** How a time is shown on screen, before lower-casing, e.g. {@code "6:00 PM"}. */
    private static final DateTimeFormatter DISPLAY_TIME =
            DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
    /** The canonical date form written to the save file, e.g. {@code "2019-10-15"}. */
    private static final DateTimeFormatter FILE_DATE =
            DateTimeFormatter.ofPattern("uuuu-MM-dd", Locale.ENGLISH);
    /** The canonical time form written to the save file, e.g. {@code "1800"}. */
    private static final DateTimeFormatter FILE_TIME =
            DateTimeFormatter.ofPattern("HHmm", Locale.ENGLISH);

    private final LocalDate date;
    private final LocalTime time;

    private TaskDateTime(LocalDate date, LocalTime time) {
        // The whole class is built around "date always present, time optional"
        // (see the class comment). Every accepted input format resolves a
        // LocalDate, so a null date here would mean a parsing bug.
        assert date != null : "a TaskDateTime always has a date; only the time may be null";
        this.date = date;
        this.time = time;
    }

    private static DateTimeFormatter strict(String pattern) {
        return DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT);
    }

    /**
     * Reads a user-supplied date/time in any of the accepted formats (see
     * {@link #FORMAT_HINT}). Whitespace around the value is ignored.
     *
     * @throws MeowmeowException if the text matches none of the formats, or
     *     names a date/time that doesn't exist on the calendar/clock.
     */
    public static TaskDateTime parse(String input) throws MeowmeowException {
        String trimmed = input.trim();
        for (InputFormat format : INPUT_FORMATS) {
            try {
                LocalDate parsedDate = LocalDate.parse(trimmed, format.formatter());
                LocalTime parsedTime = format.hasTime()
                        ? LocalTime.parse(trimmed, format.formatter())
                        : null;
                return new TaskDateTime(parsedDate, parsedTime);
            } catch (DateTimeParseException tryNextFormat) {
                // Not this format - fall through and try the next one.
            }
        }
        throw new MeowmeowException(" I don't understand that date.", FORMAT_HINT);
    }

    /** Returns the calendar date, used by the "list &lt;date&gt;" filter. */
    public LocalDate getDate() {
        return date;
    }

    /** Returns just the date part on screen, e.g. {@code "Dec 2 2019"} (no time). */
    public String toDateString() {
        return date.format(DISPLAY_DATE);
    }

    /**
     * Returns {@code true} if this point is no later than {@code other} - used
     * to check that an event's start isn't after its end. A missing time
     * counts as the start of that day (00:00).
     */
    public boolean isNotAfter(TaskDateTime other) {
        int dayComparison = date.compareTo(other.date);
        if (dayComparison != 0) {
            return dayComparison < 0;
        }
        LocalTime thisTime = time == null ? LocalTime.MIN : time;
        LocalTime otherTime = other.time == null ? LocalTime.MIN : other.time;
        return !thisTime.isAfter(otherTime);
    }

    /**
     * Returns the on-screen form, e.g. {@code "Dec 2 2019, 6:00 pm"} when a
     * time was given, or {@code "Oct 15 2019"} when only a date was given.
     */
    @Override
    public String toString() {
        String shownDate = date.format(DISPLAY_DATE);
        if (time == null) {
            return shownDate;
        }
        // DISPLAY_TIME gives "6:00 PM"; the course's expected form is lower
        // case ("6:00 pm"). Only the "AM"/"PM" letters are affected -
        // digits and the colon lower-case to themselves.
        return shownDate + ", " + time.format(DISPLAY_TIME).toLowerCase();
    }

    /**
     * Returns the save-file form, e.g. {@code "2019-12-02 1800"} or
     * {@code "2019-12-02"}. This is itself one of the accepted input formats,
     * so a saved value reloads through the same {@link #parse} with no
     * separate reader needed.
     */
    public String toFileString() {
        String storedDate = date.format(FILE_DATE);
        return time == null ? storedDate : storedDate + " " + time.format(FILE_TIME);
    }
}
