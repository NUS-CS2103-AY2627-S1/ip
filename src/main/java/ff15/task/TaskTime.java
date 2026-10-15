package ff15.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Objects;

import ff15.FF15Exception;

/**
 * The date, and optionally the time of day, attached to a task.
 * The user may type either form: {@code 2019-12-02} for a whole day, or
 * {@code 2019-12-02 1800} to pin it to 6pm. Whether a time was given is
 * remembered, so a task typed without one is never shown with a made-up
 * midnight attached to it.
 *
 * <p>This class replaces the earlier {@code Dates} helper: it owns the same
 * parsing and formatting, but now has something to store, so it holds a value
 * instead of being a bag of static methods.
 */
public class TaskTime {
    /** A date on its own, as typed and as saved, e.g. {@code 2019-12-02}. */
    private static final DateTimeFormatter DATE_INPUT = DateTimeFormatter.ISO_LOCAL_DATE;

    /**
     * A date with a time, as typed and as saved, e.g. {@code 2019-12-02 1800}.
     * Resolved strictly, so that Feb 30 is rejected rather than quietly rolled
     * back to Feb 28, which is what the default "smart" resolution does. Strict
     * resolution needs {@code uuuu} (the proleptic year) in place of {@code yyyy}.
     */
    private static final DateTimeFormatter DATE_TIME_INPUT = DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm")
            .withResolverStyle(ResolverStyle.STRICT);

    /** How the date is shown back to the user, e.g. {@code Dec 02 2019}. */
    private static final DateTimeFormatter DATE_DISPLAY = DateTimeFormatter.ofPattern("MMM dd yyyy");

    /** How the time is shown back to the user, e.g. {@code 6:00pm}. */
    private static final DateTimeFormatter TIME_DISPLAY = DateTimeFormatter.ofPattern("h:mma");

    private final LocalDateTime moment;
    private final boolean hasTime; // false when the user typed a date only

    private TaskTime(LocalDateTime moment, boolean hasTime) {
        this.moment = moment;
        this.hasTime = hasTime;
    }

    /**
     * Turns text typed by the user, or read back from the save file, into a
     * date and an optional time. A space in {@code text} means a time follows.
     *
     * @throws FF15Exception if {@code text} is not a real date, or a real date and time.
     */
    public static TaskTime parse(String text) throws FF15Exception {
        try {
            if (text.contains(" ")) {
                return new TaskTime(LocalDateTime.parse(text, DATE_TIME_INPUT), true);
            }
            // No time given: keep the date and remember that the time is unknown.
            return new TaskTime(LocalDate.parse(text, DATE_INPUT).atStartOfDay(), false);
        } catch (DateTimeParseException e) {
            throw new FF15Exception("'" + text + "' isn't a date. I know dates. I've been on a lot of dates. "
                    + "Write it as yyyy-mm-dd, or yyyy-mm-dd HHmm to add a time, "
                    + "e.g. 2019-12-02 or 2019-12-02 1800");
        }
    }

    /** Returns just the day this falls on, which is what a {@link DateRange} query matches against. */
    public LocalDate getDate() {
        return moment.toLocalDate();
    }

    /** Returns whether this moment comes before {@code other}. */
    public boolean isBefore(TaskTime other) {
        return moment.isBefore(other.moment);
    }

    /**
     * Returns whether this and {@code other} are the very same instant, whether
     * or not either was typed with a time. A date-only value stands for the start
     * of its day, so it is the same moment as that day typed with 0000.
     */
    public boolean isSameMomentAs(TaskTime other) {
        return moment.equals(other.moment);
    }

    /**
     * Two values are equal when they show the same thing: the same moment, typed
     * the same way. A date-only value and the same day at 0000 are not equal,
     * since one prints a time and the other does not.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TaskTime that)) {
            return false;
        }
        return moment.equals(that.moment) && hasTime == that.hasTime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(moment, hasTime);
    }

    /** Returns a bare date in the form used throughout the output, e.g. {@code Dec 02 2019}. */
    public static String formatDate(LocalDate date) {
        return date.format(DATE_DISPLAY);
    }

    /** Returns this in the form written to the save file, which is the same form the user types. */
    public String toFileFormat() {
        return hasTime ? moment.format(DATE_TIME_INPUT) : moment.format(DATE_INPUT);
    }

    /** Returns this as shown to the user, e.g. {@code Dec 02 2019} or {@code Dec 02 2019, 6:00pm}. */
    @Override
    public String toString() {
        if (!hasTime) {
            return moment.format(DATE_DISPLAY);
        }
        // toLowerCase turns the formatter's "PM" into the friendlier "pm".
        return moment.format(DATE_DISPLAY) + ", " + moment.format(TIME_DISPLAY).toLowerCase();
    }
}
