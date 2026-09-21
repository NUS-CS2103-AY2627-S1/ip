package maggigorengayam.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Parses and formats the dates/times used by {@link maggigorengayam.task.Deadline}
 * and {@link maggigorengayam.task.Event}. User input and the on-disk save format both use the same
 * "yyyy-MM-dd" (optionally followed by a space and a 24-hour "HHmm" time)
 * shape, e.g. "2019-12-02" or "2019-12-02 1800" - so save/load can reuse
 * {@link #parse(String)} directly instead of needing a separate format.
 */
public class DateTimeUtil {
    // STRICT (rather than the default SMART) so a non-existent calendar date like
    // "2019-02-30" is rejected outright instead of silently resolving to Feb 28.
    // Pattern uses "uuuu" (proleptic year), not "yyyy" (year-of-era): under STRICT,
    // "yyyy" needs an explicit era field to resolve, which this pattern doesn't have,
    // and parsing would fail even for an otherwise-valid date like "2019-12-02".
    private static final DateTimeFormatter INPUT_DATE_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter INPUT_TIME_FORMAT = DateTimeFormatter.ofPattern("HHmm");
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy");

    /**
     * A date with an optional time of day. {@code time} is {@code null}
     * when the input only specified a date, e.g. "2019-12-02".
     */
    public static class ParsedDateTime {
        public final LocalDate date;
        public final LocalTime time;

        /** Pairs a parsed date with its optional time. */
        public ParsedDateTime(LocalDate date, LocalTime time) {
            this.date = date;
            this.time = time;
        }
    }

    /**
     * Parses "yyyy-MM-dd" or "yyyy-MM-dd HHmm" (a single space between the
     * date and a 24-hour, 4-digit time) into a {@link ParsedDateTime}.
     *
     * @throws DateTimeParseException if the text doesn't match either shape.
     */
    public static ParsedDateTime parse(String input) throws DateTimeParseException {
        String trimmed = input.trim();
        int spaceIndex = trimmed.indexOf(' ');
        if (spaceIndex < 0) {
            LocalDate date = LocalDate.parse(trimmed, INPUT_DATE_FORMAT);
            return new ParsedDateTime(date, null);
        }
        String datePart = trimmed.substring(0, spaceIndex);
        String timePart = trimmed.substring(spaceIndex + 1).trim();
        LocalDate date = LocalDate.parse(datePart, INPUT_DATE_FORMAT);
        LocalTime time = LocalTime.parse(timePart, INPUT_TIME_FORMAT);
        return new ParsedDateTime(date, time);
    }

    /** Parses a date-only "yyyy-MM-dd" value, e.g. the argument to `on`. */
    public static LocalDate parseDateOnly(String input) throws DateTimeParseException {
        return LocalDate.parse(input.trim(), INPUT_DATE_FORMAT);
    }

    /** Parses a bare 24-hour "HHmm" value, e.g. the window start/end arguments to `freetime`. */
    public static LocalTime parseTimeOnly(String input) throws DateTimeParseException {
        return LocalTime.parse(input.trim(), INPUT_TIME_FORMAT);
    }

    /** Formats for console display, e.g. "Dec 2 2019" or "Dec 2 2019, 6pm". */
    public static String formatForDisplay(LocalDate date, LocalTime time) {
        // date is required by every caller (Deadline/Event always have one); only time
        // is legitimately optional, so a null date here signals a caller bug, not bad input.
        assert date != null : "date must not be null";
        String datePart = date.format(DISPLAY_DATE_FORMAT);
        if (time == null) {
            return datePart;
        }
        return datePart + ", " + formatTimeForDisplay(time);
    }

    /** Formats a date-only value for console display, e.g. "Dec 2 2019". */
    public static String formatDateOnlyForDisplay(LocalDate date) {
        return date.format(DISPLAY_DATE_FORMAT);
    }

    /**
     * Formats back into the same shape {@link #parse(String)} accepts, so
     * the save file can be re-loaded with no separate serialization format.
     */
    public static String formatForSave(LocalDate date, LocalTime time) {
        // Same required-date contract as formatForDisplay.
        assert date != null : "date must not be null";
        String datePart = date.format(INPUT_DATE_FORMAT);
        if (time == null) {
            return datePart;
        }
        return datePart + " " + time.format(INPUT_TIME_FORMAT);
    }

    /** e.g. 18:00 -> "6pm", 18:30 -> "6:30pm" (12-hour, lowercase, no leading zero). */
    public static String formatTimeForDisplay(LocalTime time) {
        int hour = time.getHour() % 12;
        if (hour == 0) {
            hour = 12;
        }
        String suffix = time.getHour() < 12 ? "am" : "pm";
        if (time.getMinute() == 0) {
            return hour + suffix;
        }
        return String.format("%d:%02d%s", hour, time.getMinute(), suffix);
    }
}
