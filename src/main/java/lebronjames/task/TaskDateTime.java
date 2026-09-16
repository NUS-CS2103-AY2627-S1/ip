package lebronjames.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Objects;

import lebronjames.LebronJamesException;

/**
 * Represents the date, and optionally the time, attached to a task.
 *
 * <p>The chatbot accepts several everyday ways of writing a date, so this class
 * exists to keep all of that parsing and formatting in one place instead of
 * repeating it inside {@link Deadline} and {@link Event}.
 *
 * <p>The time is optional because "return book /by 2019-10-15" is a perfectly
 * reasonable deadline. Rather than inventing a time such as midnight and later
 * printing it as if the user had typed it, the time is stored as {@code null}
 * when it was not given, and simply left out when the task is displayed.
 *
 * <p>Instances are immutable: once created, the date and time never change.
 * That makes the class safe to share and easy to reason about.
 */
public class TaskDateTime {
    /** Human-readable list of accepted formats, for use in error messages. */
    public static final String ACCEPTED_FORMATS =
            "yyyy-MM-dd or dd/mm/yyyy, optionally followed by a 24-hour time, "
                    + "for example 2019-12-02, 2/12/2019, or 2/12/2019 1800";

    /**
     * Input formats accepted from the user, tried in this order.
     *
     * <p>Each pair is a date-only format followed by the same format with a
     * 24-hour time, for example {@code 2019-12-02} and {@code 2019-12-02 1800}.
     */
    private static final DateTimeFormatter[] DATE_ONLY_FORMATS = {
        strictFormat("uuuu-MM-dd"),
        strictFormat("d/M/uuuu"),
    };
    private static final DateTimeFormatter[] DATE_TIME_FORMATS = {
        strictFormat("uuuu-MM-dd HHmm"),
        strictFormat("d/M/uuuu HHmm"),
    };

    /** Format used when writing to the save file, so saved dates read back exactly. */
    private static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FILE_TIME_FORMAT = DateTimeFormatter.ofPattern("HHmm");

    /** Format used when showing a task to the user, for example {@code Oct 15 2019}. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");
    private static final DateTimeFormatter DISPLAY_TIME_FORMAT = DateTimeFormatter.ofPattern("h:mma");

    private final LocalDate date;

    /** Time of day, or {@code null} if the user gave only a date. */
    private final LocalTime time;

    /**
     * Creates a date and time.
     *
     * <p>Private so that every instance comes from {@link #parse(String)} and is
     * therefore guaranteed to hold a real calendar date.
     *
     * @param date Calendar date of the task.
     * @param time Time of day, or {@code null} if none was given.
     */
    private TaskDateTime(LocalDate date, LocalTime time) {
        // parse() is the only caller and always supplies a date it has just
        // parsed successfully. The time is deliberately not checked: null is its
        // documented value for "the user gave a date only".
        assert date != null : "A TaskDateTime must always carry a date";

        this.date = date;
        this.time = time;
    }

    /**
     * Builds a formatter that rejects dates that do not exist.
     *
     * <p>By default a formatter resolves leniently: asked for 30 February it
     * quietly hands back 28 February, so a mistyped deadline would be stored on
     * the wrong day without any warning. STRICT makes it refuse instead, and the
     * chatbot can then tell the user what went wrong.
     *
     * <p>STRICT requires the year pattern {@code uuuu} rather than {@code yyyy},
     * because {@code yyyy} means "year within an era" and would need the era
     * (AD/BC) to be given as well.
     *
     * @param pattern Date pattern to compile.
     * @return Formatter that accepts only real calendar dates.
     */
    private static DateTimeFormatter strictFormat(String pattern) {
        return DateTimeFormatter.ofPattern(pattern).withResolverStyle(ResolverStyle.STRICT);
    }

    /**
     * Interprets text typed by the user (or read from the save file) as a date.
     *
     * @param text Date, optionally followed by a 24-hour time.
     * @return The date and time it describes.
     * @throws LebronJamesException If the text does not match any accepted format.
     */
    public static TaskDateTime parse(String text) throws LebronJamesException {
        // Callers pass either a slice of the user's command or a field read from
        // the save file. Both are always strings, so null indicates a bug rather
        // than something the user could have typed.
        assert text != null : "Text to parse as a date must not be null";

        String trimmedText = text.strip();

        for (DateTimeFormatter format : DATE_TIME_FORMATS) {
            try {
                LocalDateTime dateTime = LocalDateTime.parse(trimmedText, format);
                return new TaskDateTime(dateTime.toLocalDate(), dateTime.toLocalTime());
            } catch (DateTimeParseException exception) {
                // Not this format; fall through and try the next one.
            }
        }

        for (DateTimeFormatter format : DATE_ONLY_FORMATS) {
            try {
                return new TaskDateTime(LocalDate.parse(trimmedText, format), null);
            } catch (DateTimeParseException exception) {
                // Not this format; fall through and try the next one.
            }
        }

        throw new LebronJamesException("AIRBALL. I could not read the date '" + trimmedText
                + "'.\nPlease use " + ACCEPTED_FORMATS + ".");
    }

    /**
     * Returns the calendar date, ignoring any time of day.
     *
     * <p>Used to check whether a task falls on a date the user asked about.
     *
     * @return Date of this task.
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns this date encoded for the save file.
     *
     * <p>Always written in {@code yyyy-MM-dd} form (plus {@code HHmm} when a time
     * was given) so that {@link #parse(String)} can read it back unchanged, no
     * matter which accepted format the user originally typed.
     *
     * @return Save-file representation of this date and time.
     */
    public String toFileFormat() {
        if (time == null) {
            return date.format(FILE_DATE_FORMAT);
        }
        return date.format(FILE_DATE_FORMAT) + " " + time.format(FILE_TIME_FORMAT);
    }

    /**
     * Returns whether the given object is the same point in time.
     *
     * <p>Without this, two TaskDateTime objects parsed from the same text would
     * compare as different simply because they are different objects, and
     * duplicate detection would never match two deadlines on the same day.
     *
     * <p>A date-only value is deliberately not equal to the same date with a
     * time on it: "return book by Monday" and "return book by Monday 6pm" say
     * different things.
     *
     * @param other Object to compare with.
     * @return Whether it is a TaskDateTime with the same date and time.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TaskDateTime)) {
            return false;
        }

        TaskDateTime otherDateTime = (TaskDateTime) other;
        // The time may legitimately be null, so it cannot be compared directly.
        return date.equals(otherDateTime.date) && Objects.equals(time, otherDateTime.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, time);
    }

    @Override
    public String toString() {
        if (time == null) {
            return date.format(DISPLAY_DATE_FORMAT);
        }
        return date.format(DISPLAY_DATE_FORMAT) + ", " + time.format(DISPLAY_TIME_FORMAT).toLowerCase();
    }
}
