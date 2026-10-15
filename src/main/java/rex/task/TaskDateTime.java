package rex.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * A point in time that a task falls on: a date, optionally with a time of day.
 *
 * The time is optional because the two kinds of task want different things.
 * "Return the book by 2019-10-15" is a whole day, while "meet from 2019-10-15
 * 1400" is a moment within one. Rather than force a time on every task, this
 * class remembers whether one was given and shows it only when it was.
 *
 * The value is always stored as a LocalDateTime (using the start of the day
 * when no time was supplied) with a separate flag, instead of a LocalDate plus
 * a LocalTime that may be null. That way no code has to guard against a null
 * before comparing or printing.
 */
public class TaskDateTime {
    /**
     * Every formatter below is pinned to one locale, because a formatter left
     * unpinned follows whatever locale the machine happens to use: the same
     * pattern prints "6:00pm" in Singapore, "6:00PM" in the United States and
     * "Okt. 15 2019" in Germany. Fixing the locale means the program prints
     * the same thing wherever it is run.
     */
    private static final Locale FORMAT_LOCALE = Locale.US;

    /**
     * The format used for a date with a time, both when reading what the user
     * typed and when writing to the save file. Being the same in both
     * directions is what lets a saved task be read back unchanged.
     */
    private static final DateTimeFormatter WITH_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm", FORMAT_LOCALE);

    /** How a date is shown to the user when no time was given, e.g. "Oct 15 2019". */
    private static final DateTimeFormatter DISPLAY_DATE_ONLY =
            DateTimeFormatter.ofPattern("MMM dd yyyy", FORMAT_LOCALE);

    /** How a date is shown to the user when a time was given, e.g. "Oct 15 2019, 6:00PM". */
    private static final DateTimeFormatter DISPLAY_WITH_TIME =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma", FORMAT_LOCALE);

    private final LocalDateTime dateTime;
    private final boolean hasTime;

    /** Private so that every instance comes from parse(), which validates its input. */
    private TaskDateTime(LocalDateTime dateTime, boolean hasTime) {
        this.dateTime = dateTime;
        this.hasTime = hasTime;
    }

    /**
     * Reads a date, with or without a time of day.
     *
     * Whether a time is present is decided by looking for a space: "2019-10-15"
     * is a date on its own, while "2019-10-15 1800" carries a 24-hour time.
     *
     * @param input the text to read, e.g. "2019-10-15" or "2019-10-15 1800".
     * @return the date it describes.
     * @throws IllegalArgumentException if the text is not a date in either
     *     format. Note that java.time itself throws DateTimeParseException,
     *     which is converted here: IllegalArgumentException is the exception
     *     both callers already handle, so neither needs to know that a
     *     date is what failed to parse.
     */
    public static TaskDateTime parse(String input) {
        String trimmed = input.trim();
        try {
            if (trimmed.contains(" ")) {
                return new TaskDateTime(LocalDateTime.parse(trimmed, WITH_TIME_FORMAT), true);
            }
            // LocalDate.parse reads yyyy-mm-dd without needing a formatter,
            // because that is its own default format.
            return new TaskDateTime(LocalDate.parse(trimmed).atStartOfDay(), false);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Not a date: " + trimmed);
        }
    }

    /**
     * Returns a plain day in the same format used to show a task's date, e.g.
     * "Oct 15 2019".
     *
     * This lives here so that every date the user sees is formatted by this
     * class, rather than the display format being repeated wherever a date is
     * printed.
     */
    public static String formatDate(LocalDate date) {
        return DISPLAY_DATE_ONLY.format(date);
    }

    /** Returns the day this falls on, ignoring any time of day. */
    public LocalDate getDate() {
        return dateTime.toLocalDate();
    }

    /**
     * Returns true if this falls on the given day. A time of day, if there is
     * one, is ignored: a deadline at 6pm on the 15th is still on the 15th.
     */
    public boolean isOn(LocalDate date) {
        return getDate().equals(date);
    }

    /**
     * Returns true if this comes before the other date.
     *
     * The times of day are compared only when both dates carry one. A date
     * written without a time stands for the whole day rather than for its
     * midnight, so "2019-10-15 1400" does not come after a plain "2019-10-15":
     * they are the same day, and an event running from the one to the other
     * is a reasonable thing to write.
     *
     * @param other the date to compare against.
     */
    public boolean isBefore(TaskDateTime other) {
        if (hasTime && other.hasTime) {
            return dateTime.isBefore(other.dateTime);
        }
        return getDate().isBefore(other.getDate());
    }

    /**
     * Returns this date as it should be written to the save file, in the same
     * format parse() reads, so that saving and loading leave it unchanged.
     */
    public String toSaveFormat() {
        return hasTime ? WITH_TIME_FORMAT.format(dateTime) : getDate().toString();
    }

    /** Returns this date as it should be shown to the user. */
    @Override
    public String toString() {
        return hasTime ? DISPLAY_WITH_TIME.format(dateTime) : DISPLAY_DATE_ONLY.format(dateTime);
    }
}
