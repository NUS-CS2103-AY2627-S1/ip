package meowmeow.task;

import meowmeow.MeowMeowException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a date, optionally with a time, attached to a task.
 * Accepts several input formats and renders dates in a readable form.
 */
public class TaskDateTime {
    private static final DateTimeFormatter[] DATE_TIME_FORMATS = {
            strict("uuuu-MM-dd HHmm"),
            strict("uuuu-MM-dd HH:mm"),
            strict("d/M/uuuu HHmm"),
            strict("d/M/uuuu HH:mm")
    };

    private static final DateTimeFormatter[] DATE_FORMATS = {
            strict("uuuu-MM-dd"),
            strict("d/M/uuuu")
    };

    private static final DateTimeFormatter OUTPUT_DATE =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter OUTPUT_DATE_TIME =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma", Locale.ENGLISH);

    private static final Map<String, DayOfWeek> DAY_NAMES = Map.ofEntries(
            Map.entry("mon", DayOfWeek.MONDAY),
            Map.entry("monday", DayOfWeek.MONDAY),
            Map.entry("tue", DayOfWeek.TUESDAY),
            Map.entry("tuesday", DayOfWeek.TUESDAY),
            Map.entry("wed", DayOfWeek.WEDNESDAY),
            Map.entry("wednesday", DayOfWeek.WEDNESDAY),
            Map.entry("thu", DayOfWeek.THURSDAY),
            Map.entry("thursday", DayOfWeek.THURSDAY),
            Map.entry("fri", DayOfWeek.FRIDAY),
            Map.entry("friday", DayOfWeek.FRIDAY),
            Map.entry("sat", DayOfWeek.SATURDAY),
            Map.entry("saturday", DayOfWeek.SATURDAY),
            Map.entry("sun", DayOfWeek.SUNDAY),
            Map.entry("sunday", DayOfWeek.SUNDAY)
    );

    private final LocalDateTime dateTime;
    private final boolean hasTime;

    private TaskDateTime(LocalDateTime dateTime, boolean hasTime) {
        this.dateTime = dateTime;
        this.hasTime = hasTime;
    }

    /**
     * Returns a date parsed from user input.
     * Accepts yyyy-MM-dd or d/M/yyyy, each optionally followed by a time
     * in HHmm or HH:mm form.
     *
     * @param input Text typed by the user.
     * @return Parsed date, with or without a time component.
     * @throws MeowMeowException If the input matches none of the accepted formats.
     */
    public static TaskDateTime parse(String input) throws MeowMeowException {
        String trimmed = input.trim();

        DayOfWeek dayOfWeek = DAY_NAMES.get(trimmed.toLowerCase(Locale.ENGLISH));
        if (dayOfWeek != null) {
            LocalDate next = LocalDate.now().with(TemporalAdjusters.next(dayOfWeek));
            return new TaskDateTime(next.atStartOfDay(), false);
        }

        for (DateTimeFormatter format : DATE_TIME_FORMATS) {
            try {
                return new TaskDateTime(LocalDateTime.parse(trimmed, format), true);
            } catch (DateTimeParseException e) {
                // try the next format
            }
        }

        for (DateTimeFormatter format : DATE_FORMATS) {
            try {
                return new TaskDateTime(LocalDate.parse(trimmed, format).atStartOfDay(), false);
            } catch (DateTimeParseException e) {
                // try the next format
            }
        }

        throw new MeowMeowException("I can't read \"" + trimmed + "\" as a date. "
                + "Try yyyy-MM-dd or d/M/yyyy, optionally with a time, "
                + "e.g. 2019-12-02 1800, or a day name like fri.");
    }

    /**
     * Returns a date decoded from its stored representation.
     *
     * @param stored Text as written by {@link #toStorageFormat()}.
     * @return Decoded date.
     * @throws MeowMeowException If the stored text cannot be decoded.
     */
    public static TaskDateTime fromStorage(String stored) throws MeowMeowException {
        try {
            if (stored.contains("T")) {
                return new TaskDateTime(LocalDateTime.parse(stored), true);
            }
            return new TaskDateTime(LocalDate.parse(stored).atStartOfDay(), false);
        } catch (DateTimeParseException e) {
            throw new MeowMeowException("Unreadable date in save file: " + stored);
        }
    }

    /**
     * Returns this date encoded for storage in the save file.
     * The encoding preserves whether a time component was supplied.
     *
     * @return ISO-formatted date, with time if one was given.
     */
    public String toStorageFormat() {
        return hasTime ? dateTime.toString() : dateTime.toLocalDate().toString();
    }

    /**
     * Returns the date part of this value, discarding any time component.
     *
     * @return The calendar date.
     */
    public LocalDate toLocalDate() {
        return dateTime.toLocalDate();
    }

    /**
     * Returns whether this date and time falls strictly before the given one.
     *
     * @param other Date and time to compare against.
     * @return True if this one is earlier.
     */
    public boolean isBefore(TaskDateTime other) {
        return dateTime.isBefore(other.dateTime);
    }

    /**
     * Returns whether this date and time has already passed.
     * Values given without a time are compared by date only, so a date-only
     * value for today has not yet passed.
     *
     * @return True if this date and time is in the past.
     */
    public boolean isInThePast() {
        if (hasTime) {
            return dateTime.isBefore(LocalDateTime.now());
        }
        return dateTime.toLocalDate().isBefore(LocalDate.now());
    }

    /**
     * Returns a formatter for the given pattern that rejects invalid dates
     * rather than adjusting them to the nearest valid one.
     *
     * @param pattern Date or date-time pattern, using uuuu for the year.
     * @return Strictly resolving formatter.
     */
    private static DateTimeFormatter strict(String pattern) {
        return DateTimeFormatter.ofPattern(pattern).withResolverStyle(ResolverStyle.STRICT);
    }

    @Override
    public String toString() {
        return hasTime ? dateTime.format(OUTPUT_DATE_TIME) : dateTime.format(OUTPUT_DATE);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TaskDateTime otherDateTime)) {
            return false;
        }
        return hasTime == otherDateTime.hasTime && dateTime.equals(otherDateTime.dateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateTime, hasTime);
    }
}