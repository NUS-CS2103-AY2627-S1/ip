package chud.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * A calendar date, optionally paired with a time of day, used for a deadline's {@code /by} and
 * an event's {@code /from}/{@code /to} fields. Understands several common textual formats (see
 * {@link #parse}), displays itself in a friendly format (e.g. "Oct 15 2019" or
 * "Dec 02 2019, 6:00PM"), and can round-trip itself to/from the plain-text save file.
 */
public class TaskDateTime {
    // Parsed with STRICT resolution so a nonexistent calendar date (e.g. "2019-02-30") is
    // rejected outright, rather than java.time's default SMART mode silently clamping it to
    // the nearest valid date (Feb 28). The year is matched with 'u' (proleptic year) rather
    // than 'y' (year-of-era): under STRICT, 'y' requires an era to resolve and fails parsing
    // without one, while 'u' resolves unambiguously on its own.
    private static final DateTimeFormatter[] INPUT_DATE_TIME_FORMATS = {
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm").withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm").withResolverStyle(ResolverStyle.STRICT),
    };
    private static final DateTimeFormatter[] INPUT_DATE_ONLY_FORMATS = {
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT),
    };
    private static final DateTimeFormatter DISPLAY_DATE_ONLY = DateTimeFormatter.ofPattern("MMM dd yyyy");
    private static final DateTimeFormatter DISPLAY_DATE_TIME =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma");
    private static final DateTimeFormatter STORAGE_DATE_ONLY = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter STORAGE_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    private final LocalDate date;
    private final LocalTime time; // null if no time of day was given

    private TaskDateTime(LocalDate date, LocalTime time) {
        // A TaskDateTime always has a date -- only the time of day is optional (null means
        // "date only"). getDate(), toString(), and toStorageString() all rely on date being
        // non-null without checking it themselves.
        assert date != null : "TaskDateTime must always have a date";
        this.date = date;
        this.time = time;
    }

    /**
     * Parses text such as {@code "2019-10-15"}, {@code "2019-10-15 1800"}, {@code "2/12/2019"},
     * or {@code "2/12/2019 1800"} into a TaskDateTime.
     *
     * @throws IllegalArgumentException if the text doesn't match any accepted format, with a
     *      message listing the accepted formats so the caller can show the user how to fix it.
     */
    public static TaskDateTime parse(String text) {
        String trimmed = text.trim();
        for (DateTimeFormatter format : INPUT_DATE_TIME_FORMATS) {
            try {
                LocalDateTime dateTime = LocalDateTime.parse(trimmed, format);
                return new TaskDateTime(dateTime.toLocalDate(), dateTime.toLocalTime());
            } catch (DateTimeParseException e) {
                // Not this format -- fall through and try the next one.
            }
        }
        for (DateTimeFormatter format : INPUT_DATE_ONLY_FORMATS) {
            try {
                return new TaskDateTime(LocalDate.parse(trimmed, format), null);
            } catch (DateTimeParseException e) {
                // Not this format -- fall through and try the next one.
            }
        }
        throw new IllegalArgumentException("'" + text + "' isn't a date/time I understand. "
                + "Try a format like 2019-10-15, 2019-10-15 1800, 2/12/2019, or 2/12/2019 1800.");
    }

    /** Returns the calendar date, ignoring any time of day. */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns true if this date/time is strictly earlier than other's, for validating that an
     * event's end isn't before its start. A missing time of day is treated as midnight (the
     * start of that date) for the comparison, consistent with how a date-only value is otherwise
     * treated as covering the whole day.
     */
    public boolean isBefore(TaskDateTime other) {
        LocalDateTime thisDateTime = LocalDateTime.of(date, time == null ? LocalTime.MIN : time);
        LocalDateTime otherDateTime = LocalDateTime.of(other.date, other.time == null ? LocalTime.MIN : other.time);
        return thisDateTime.isBefore(otherDateTime);
    }

    /** Encodes this date/time for the plain-text save file, in a form {@link #parse} understands. */
    public String toStorageString() {
        return time == null
                ? date.format(STORAGE_DATE_ONLY)
                : LocalDateTime.of(date, time).format(STORAGE_DATE_TIME);
    }

    @Override
    public String toString() {
        return time == null
                ? date.format(DISPLAY_DATE_ONLY)
                : LocalDateTime.of(date, time).format(DISPLAY_DATE_TIME);
    }
}
