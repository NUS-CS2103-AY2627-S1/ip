package bot.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import bot.exception.BotException;

/**
 * Represents a date, optionally paired with a time of day, used for a Deadline's
 * "by" or an Event's "from"/"to". Understanding these as real dates
 * (rather than free-text strings) is what lets Bot format them
 * consistently for display and, e.g., answer "what's happening on this
 * date" (see {@link bot.task.Task#occursOn}).
 *
 * <p>Two input shapes are accepted, both also used when saving to disk so
 * a saved file can be read back exactly:
 * <ul>
 *   <li>{@code yyyy-MM-dd}, e.g. {@code 2019-10-15} (date only), or</li>
 *   <li>{@code yyyy-MM-dd HHmm}, e.g. {@code 2019-10-15 1800} (date and a
 *       24-hour time).</li>
 * </ul>
 * Whichever shape was given controls how it's later displayed: a
 * date-only value prints as e.g. {@code Oct 15 2019}, while a value with
 * a time prints as e.g. {@code Oct 15 2019, 6:00PM}.
 */
public class TaskDateTime {
    // Pinned to Locale.US so month names and the AM/PM marker are always
    // rendered the same way (e.g. "Oct" and "PM"), regardless of the JVM's
    // default locale - otherwise both differ by machine/JDK version, which
    // once caused e.g. "6:00pm" instead of "6:00PM" on this machine's
    // default locale.
    private static final DateTimeFormatter INPUT_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US);
    private static final DateTimeFormatter INPUT_DATE_TIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm", Locale.US);
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.US);
    private static final DateTimeFormatter DISPLAY_DATE_TIME =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma", Locale.US);

    private final LocalDateTime dateTime;
    private final boolean hasTime;

    private TaskDateTime(LocalDateTime dateTime, boolean hasTime) {
        // Both call sites in parse() build dateTime via
        // LocalDateTime.parse/LocalDate.atStartOfDay, neither of which
        // returns null (they throw DateTimeParseException instead) - so
        // this assumption should always hold for this private constructor.
        assert dateTime != null : "dateTime must not be null";
        this.dateTime = dateTime;
        this.hasTime = hasTime;
    }

    /**
     * Parses a date or date-time argument as typed by the user (or read
     * back from the data file, since both use the same two shapes).
     *
     * @throws BotException if the text matches neither accepted shape
     */
    public static TaskDateTime parse(String text) throws BotException {
        String trimmed = text.trim();
        try {
            return new TaskDateTime(LocalDateTime.parse(trimmed, INPUT_DATE_TIME), true);
        } catch (DateTimeParseException ignoredDateTimeFailure) {
            try {
                return new TaskDateTime(LocalDate.parse(trimmed, INPUT_DATE).atStartOfDay(), false);
            } catch (DateTimeParseException ignoredDateFailure) {
                throw new BotException("OOPS!!! \"" + trimmed + "\" isn't a date I understand - use "
                        + "yyyy-MM-dd (e.g. 2019-10-15) or yyyy-MM-dd HHmm (e.g. 2019-10-15 1800).");
            }
        }
    }

    /**
     * Parses a plain date argument (no time component), as used by the
     * {@code on} command to look up tasks by calendar date.
     *
     * @throws BotException if the text isn't in {@code yyyy-MM-dd} form
     */
    public static LocalDate parseDateOnly(String text) throws BotException {
        String trimmed = text.trim();
        try {
            return LocalDate.parse(trimmed, INPUT_DATE);
        } catch (DateTimeParseException e) {
            throw new BotException(
                    "OOPS!!! \"" + trimmed + "\" isn't a date I understand - use yyyy-MM-dd (e.g. 2019-10-15).");
        }
    }

    /** Formats a plain date the same way a TaskDateTime without a time would print. */
    public static String formatDateOnly(LocalDate date) {
        return date.format(DISPLAY_DATE);
    }

    /**
     * Returns the calendar date part of this value, discarding any time of day.
     */
    public LocalDate toLocalDate() {
        return dateTime.toLocalDate();
    }

    /** Returns whether this value occurs strictly after {@code other}. */
    public boolean isAfter(TaskDateTime other) {
        return dateTime.isAfter(other.dateTime);
    }

    /** The text form written to the data file; parseable back via {@link #parse}. */
    public String toSaveFormat() {
        return hasTime ? dateTime.format(INPUT_DATE_TIME) : dateTime.format(INPUT_DATE);
    }

    /** The human-friendly form shown in task listings, e.g. "Oct 15 2019" or "Oct 15 2019, 6:00PM". */
    @Override
    public String toString() {
        return hasTime ? dateTime.format(DISPLAY_DATE_TIME) : dateTime.format(DISPLAY_DATE);
    }
}
