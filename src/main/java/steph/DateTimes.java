package steph;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Helpers for reading and displaying the date-and-time values attached to
 * Deadline and Event tasks. Kept in one place so the command parser
 * ({@link Parser}), the save-file reader ({@link Storage}), and the task
 * classes all agree on how a date-time is written and shown.
 */
public final class DateTimes {

    /**
     * User input format for the optional time part: an ISO date, a space, then
     * a 24-hour "HHmm", e.g. "2019-10-15 1800".
     */
    private static final DateTimeFormatter INPUT_WITH_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    /** Display format when a time is present, e.g. "Oct 15 2019, 6:00PM". */
    private static final DateTimeFormatter DISPLAY_WITH_TIME = DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma");

    /** Display format when the time is midnight (i.e. the user gave only a date), e.g. "Oct 15 2019". */
    private static final DateTimeFormatter DISPLAY_DATE_ONLY = DateTimeFormatter.ofPattern("MMM dd yyyy");

    private DateTimes() {
        // Utility class: all members are static, so there's nothing to instantiate.
    }

    /**
     * Parses text the user typed after {@code /by}, {@code /from}, or {@code /to}.
     * Accepts "yyyy-mm-dd" or "yyyy-mm-dd HHmm"; when only a date is given the
     * time defaults to midnight (00:00).
     *
     * @param text The date (and optional time) as typed by the user.
     * @return The parsed date-time.
     * @throws DateTimeParseException If the text matches neither accepted format.
     */
    static LocalDateTime parseUserInput(String text) throws DateTimeParseException {
        try {
            return LocalDateTime.parse(text, INPUT_WITH_TIME);
        } catch (DateTimeParseException timePartMissing) {
            // No "HHmm" part -- fall back to a plain date at the start of the day.
            return LocalDate.parse(text).atStartOfDay();
        }
    }

    /**
     * Reads a value previously written by {@link #toStorageFormat}. Accepts the
     * ISO {@code LocalDateTime} form ("2019-10-15T18:00") and a plain ISO
     * date ("2019-10-15").
     *
     * @param text One date-time field from a save-file line.
     * @return The parsed date-time.
     * @throws DateTimeParseException If the text is neither form.
     */
    static LocalDateTime parseStored(String text) throws DateTimeParseException {
        try {
            return LocalDateTime.parse(text);
        } catch (DateTimeParseException notDateTime) {
            return LocalDate.parse(text).atStartOfDay();
        }
    }

    /**
     * Formats a date-time for the save file. {@code LocalDateTime.toString()} is
     * ISO-8601 ("2019-10-15T18:00"), which {@link #parseStored} reads back
     * without a formatter, so the file stays round-trippable.
     *
     * @param dateTime The value to write.
     * @return The ISO-8601 text.
     */
    public static String toStorageFormat(LocalDateTime dateTime) {
        return dateTime.toString();
    }

    /**
     * Formats a date-time for display, omitting the time when it is midnight
     * (which is what a date-only command produces).
     *
     * @param dateTime The value to show.
     * @return The display text, e.g. "Oct 15 2019" or "Oct 15 2019, 6:00PM".
     */
    public static String toDisplayFormat(LocalDateTime dateTime) {
        if (dateTime.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return dateTime.format(DISPLAY_DATE_ONLY);
        }
        return dateTime.format(DISPLAY_WITH_TIME);
    }

    /**
     * Returns {@code boundary}, or the start of the next day if {@code boundary}
     * is exactly midnight. A midnight boundary usually comes from a date-only
     * user input (e.g. "2019-10-15" with no time); as the end of an event's
     * span, that's meant to mean "through the end of that day" rather than the
     * single instant the day begins. Used both to check whether two events'
     * spans overlap (see {@link steph.task.Event#clashesWith}) and whether an
     * event's start comes before its end (see {@link Parser#parseEvent}).
     *
     * @param boundary The date-time to expand if it falls on midnight.
     * @return The expanded date-time, or {@code boundary} unchanged.
     */
    public static LocalDateTime expandIfMidnight(LocalDateTime boundary) {
        return boundary.toLocalTime().equals(LocalTime.MIDNIGHT) ? boundary.plusDays(1) : boundary;
    }
}
