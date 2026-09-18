package kenbot.task;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import kenbot.KenbotException;

/**
 * A point in time attached to a task: a real date, and optionally some text
 * describing the time of day.
 *
 * <p>The date is held as a {@link java.time.LocalDate}, so it is a genuine date
 * rather than text that happens to look like one. The time of day is kept as
 * plain text, because a task only ever displays it or saves it.</p>
 *
 * <p>Deadlines and events both need this, so it lives here once instead of
 * being repeated in each of them.</p>
 */
public class TaskDate {

    /** How a date is shown on screen, for example {@code Oct 15 2019}. */
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ROOT);

    private final LocalDate date;

    /** The time of day as the user typed it, or empty if none was given. */
    private final String time;

    /**
     * Creates a task date.
     *
     * <p>Private so that every task date has to come from {@link #of}, which
     * means one can never exist without a valid date inside it.</p>
     *
     * @param date the date
     * @param time the time of day as text, or an empty string if there is none
     */
    private TaskDate(LocalDate date, String time) {
        // Both toString() and toStorable() branch on time.isEmpty() rather than
        // on null, so a null here would not read as "no time given" -- it would
        // throw. The one factory method is careful to pass "" instead, and this
        // records that the two must stay in step.
        assert time != null : "time is empty when absent, never null";
        this.date = date;
        this.time = time;
    }

    /**
     * Creates a task date from text the user typed or the save file holds.
     *
     * <p>The text is split at the first space. The first part must be a date
     * written as {@code yyyy-mm-dd}; anything after it is kept as the time of
     * day without being checked.</p>
     *
     * @param text a date, optionally followed by a time
     * @return the new task date
     * @throws KenbotException if the text is blank or does not start with a date
     */
    public static TaskDate of(String text) throws KenbotException {
        if (text.isBlank()) {
            throw new KenbotException("Need a date written as yyyy-mm-dd,"
                    + " like: 2019-10-15");
        }

        String[] parts = text.trim().split("\\s+", 2);
        String time = parts.length > 1 ? parts[1] : "";

        try {
            return new TaskDate(LocalDate.parse(parts[0]), time);
        } catch (DateTimeParseException e) {
            // Java's own error is turned into ours, so callers only ever have
            // one kind of problem to handle.
            throw new KenbotException("'" + parts[0] + "' isn't a date I can read."
                    + " Write it as yyyy-mm-dd, like: 2019-10-15 1800");
        }
    }

    /**
     * Returns whether this point in time comes after another.
     *
     * <p>The date decides it. When both fall on the same day the time of day is
     * used instead, but only when both were written as a real time: the time is
     * kept as the user typed it, so it may be something like "after lunch" that
     * cannot be compared. Two points that cannot be ordered are reported as not
     * after each other, which lets an event through rather than refusing
     * something that may well be fine.</p>
     *
     * @param other the point in time to compare with
     * @return true if this is definitely later than the other
     */
    public boolean isAfter(TaskDate other) {
        if (!date.equals(other.date)) {
            return date.isAfter(other.date);
        }

        LocalTime mine = asLocalTime();
        LocalTime theirs = other.asLocalTime();
        if (mine == null || theirs == null) {
            return false;
        }
        return mine.isAfter(theirs);
    }

    /**
     * Returns the time of day as a real time, or null if it was not written as
     * one.
     *
     * @return the parsed time, or null when it cannot be read as a time
     */
    private LocalTime asLocalTime() {
        if (time.isEmpty()) {
            return null;
        }
        for (String pattern : new String[] {"HHmm", "HH:mm", "H:mm"}) {
            try {
                return LocalTime.parse(time, DateTimeFormatter.ofPattern(pattern, Locale.ROOT));
            } catch (DateTimeParseException e) {
                // Try the next shape; a time nobody can read is not an error
                // here, it just means the two cannot be ordered.
                continue;
            }
        }
        return null;
    }

    /**
     * Returns this date as it should be written to the save file.
     *
     * <p>The plain numeric form is used so that {@link #of} can read it straight
     * back. The screen format could not be read back.</p>
     *
     * @return the save-file text for this date
     */
    public String toStorable() {
        return time.isEmpty() ? date.toString() : date + " " + time;
    }

    /**
     * Returns this date as it should be shown on screen.
     *
     * @return the date as {@code MMM dd yyyy}, followed by the time if given
     */
    @Override
    public String toString() {
        String shown = date.format(DISPLAY_FORMAT);
        return time.isEmpty() ? shown : shown + " " + time;
    }
}
