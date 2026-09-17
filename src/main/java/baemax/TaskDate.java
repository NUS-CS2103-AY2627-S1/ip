package baemax;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * A calendar date for a task, optionally carrying a time of day. Accepts the
 * range of date and time formats a user is likely to type and prints them back
 * in one consistent style.
 */
public class TaskDate {
    /** Date-only input formats that are tried in order. */
    private static final DateTimeFormatter[] DATE_FORMATS = {
        ofPattern("yyyy-MM-dd"),
        ofPattern("d/M/yyyy"),
        ofPattern("d-M-yyyy"),
        ofPattern("d MMM yyyy"),
        ofPattern("MMM d yyyy"),
        ofPattern("d MMMM yyyy"),
        ofPattern("MMMM d yyyy"),
    };

    /** Time-of-day input formats that are tried in order for a trailing token. */
    private static final DateTimeFormatter[] TIME_FORMATS = {
        ofPattern("HHmm"),
        ofPattern("H:mm"),
        ofPattern("h:mma"),
        ofPattern("ha"),
    };

    /** How a date with no time is shown, e.g. {@code Oct 15 2019}. */
    private static final DateTimeFormatter DISPLAY_DATE = ofPattern("MMM dd yyyy");

    /** How a date with a time is shown, e.g. {@code Oct 15 2019, 6:00PM}. */
    private static final DateTimeFormatter DISPLAY_DATE_TIME = ofPattern("MMM dd yyyy, h:mma");

    /** The date part, always present. */
    private final LocalDate date;

    /** The time of day, or {@code null} when the user gave only a date. */
    private final LocalTime time;

    /**
     * Creates a task date from its parts.
     *
     * @param date the date part
     * @param time the time of day, or {@code null} for a date-only value
     */
    private TaskDate(LocalDate date, LocalTime time) {
        this.date = date;
        this.time = time;
    }

    /**
     * Parses a date the user typed, such as {@code 2019-10-15},
     * {@code 15/10/2019}, {@code 15 Oct 2019}, or any of those followed by a
     * time such as {@code 1800}, {@code 18:00}, or {@code 6pm}.
     *
     * @param raw the date text entered by the user
     * @return the parsed date, with a time if one was given
     * @throws BaemaxException when the text cannot be read as a date
     */
    public static TaskDate parse(String raw) throws BaemaxException {
        String trimmed = raw.trim();

        int lastSpace = trimmed.lastIndexOf(' ');
        if (lastSpace > 0) {
            LocalTime maybeTime = tryParseTime(trimmed.substring(lastSpace + 1).trim());
            if (maybeTime != null) {
                LocalDate datePart = tryParseDate(trimmed.substring(0, lastSpace).trim());
                if (datePart != null) {
                    return new TaskDate(datePart, maybeTime);
                }
            }
        }

        LocalDate dateOnly = tryParseDate(trimmed);
        if (dateOnly != null) {
            return new TaskDate(dateOnly, null);
        }

        throw new BaemaxException(
                "Baemax could not read \"" + raw + "\" as a date. "
                + "Try 2019-10-15, 15/10/2019, or 15 Oct 2019 1800.");
    }

    /**
     * Rebuilds a date from the save file, where it is stored in ISO form:
     * {@code 2019-10-15} for a date, {@code 2019-10-15T18:00} with a time.
     *
     * @param stored the ISO text from the save file
     * @return the stored date
     * @throws BaemaxException when the text is not valid ISO date or date-time
     */
    public static TaskDate fromStorage(String stored) throws BaemaxException {
        try {
            return new TaskDate(LocalDate.parse(stored), null);
        } catch (DateTimeParseException dateOnlyFailed) {
            try {
                LocalDateTime dateTime = LocalDateTime.parse(stored);
                return new TaskDate(dateTime.toLocalDate(), dateTime.toLocalTime());
            } catch (DateTimeParseException dateTimeFailed) {
                throw new BaemaxException("Unreadable date in the save file: " + stored);
            }
        }
    }

    /**
     * Returns the ISO text used to store this date in the save file.
     *
     * @return {@code yyyy-MM-dd}, or {@code yyyy-MM-ddTHH:mm} when a time is set
     */
    public String toStorageString() {
        return time == null ? date.toString() : LocalDateTime.of(date, time).toString();
    }

    /**
     * Formats this date for display: {@code MMM dd yyyy}, with
     * {@code , h:mma} appended when a time is set.
     *
     * @return the display text
     */
    @Override
    public String toString() {
        return time == null
                ? date.format(DISPLAY_DATE)
                : LocalDateTime.of(date, time).format(DISPLAY_DATE_TIME);
    }

    /**
     * Returns whether this date/time is strictly after {@code other}, treating
     * a date with no time as the very start of that day.
     *
     * @param other the date/time to compare against
     * @return {@code true} when this date/time comes after {@code other}
     */
    public boolean isAfter(TaskDate other) {
        return toComparable().isAfter(other.toComparable());
    }

    /**
     * Returns this date/time as a {@link LocalDateTime}, so two dates can be
     * compared even when only one of them carries a time.
     *
     * @return this date at its time, or at midnight when no time is set
     */
    private LocalDateTime toComparable() {
        return LocalDateTime.of(date, time == null ? LocalTime.MIDNIGHT : time);
    }

    /**
     * Tries each accepted date format in turn.
     *
     * @param text the candidate date text
     * @return the parsed date, or {@code null} if no format matched
     */
    private static LocalDate tryParseDate(String text) {
        for (DateTimeFormatter format : DATE_FORMATS) {
            try {
                return LocalDate.parse(text, format);
            } catch (DateTimeParseException tryNext) {
                // fall through to the next format
            }
        }
        return null;
    }

    /**
     * Tries each accepted time-of-day format in turn.
     *
     * @param text the candidate time text
     * @return the parsed time, or {@code null} if no format matched
     */
    private static LocalTime tryParseTime(String text) {
        for (DateTimeFormatter format : TIME_FORMATS) {
            try {
                return LocalTime.parse(text, format);
            } catch (DateTimeParseException tryNext) {
                // fall through to the next format
            }
        }
        return null;
    }

    /**
     * Builds a case-insensitive, English-locale formatter for the pattern.
     *
     * @param pattern a {@link DateTimeFormatter} pattern
     * @return the formatter
     */
    private static DateTimeFormatter ofPattern(String pattern) {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(pattern)
                .toFormatter(Locale.ENGLISH);
    }
}
