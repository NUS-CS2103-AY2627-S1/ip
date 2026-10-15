package wodan.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

import wodan.WodanException;

/**
 * A calendar date, optionally with a time of day, stored as a {@link LocalDateTime}.
 */
public class TaskDateTime {
    /**
     * Example date formats shown in parse-error messages.
     */
    public static final String HINT = "2026-08-26 or 08/26/2019 1800";

    private static final DateTimeFormatter[] DATE_TIME_FORMATTERS = {
        DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm")
                .withResolverStyle(ResolverStyle.STRICT),
        DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm")
                .withResolverStyle(ResolverStyle.STRICT),
        DateTimeFormatter.ofPattern("d/M/uuuu HHmm")
                .withResolverStyle(ResolverStyle.STRICT),
        DateTimeFormatter.ofPattern("d/M/uuuu HH:mm")
                .withResolverStyle(ResolverStyle.STRICT)
    };
    private static final DateTimeFormatter[] DATE_FORMATTERS = {
        DateTimeFormatter.ISO_LOCAL_DATE,
        DateTimeFormatter.ofPattern("d/M/uuuu")
                .withResolverStyle(ResolverStyle.STRICT)
    };
    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_DATE_TIME =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma", Locale.ENGLISH);
    private static final DateTimeFormatter STORAGE_DATE_TIME =
            DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm");

    private final LocalDateTime dateTime;
    private final boolean hasTime;

    /**
     * Creates a date-time value.
     *
     * @param dateTime Date and time to store. Midnight if {@code hasTime} is false.
     * @param hasTime Whether a time of day was specified.
     */
    public TaskDateTime(LocalDateTime dateTime, boolean hasTime) {
        assert dateTime != null : "dateTime should not be null";
        assert hasTime || dateTime.toLocalTime().equals(LocalTime.MIDNIGHT)
                : "Date-only values are stored at midnight";
        this.dateTime = dateTime;
        this.hasTime = hasTime;
    }

    /**
     * Returns a parsed date-time from user input.
     *
     * @param text Raw date or date-time text.
     * @return The parsed value.
     * @throws WodanException If {@code text} is not a supported date or date-time.
     */
    public static TaskDateTime parse(String text) throws WodanException {
        TaskDateTime parsed = tryParse(text);
        if (parsed == null) {
            throw new WodanException(
                    "The ravens cannot read '" + text + "'. Try: " + HINT);
        }
        return parsed;
    }

    /**
     * Returns a parsed date-time from a save-file field, or {@code null} if it is invalid.
     *
     * @param text Stored date or date-time text.
     * @return The parsed value, or {@code null} if {@code text} cannot be parsed.
     */
    public static TaskDateTime parseStorage(String text) {
        return tryParse(text);
    }

    /**
     * Returns {@code date} formatted as {@code MMM dd yyyy}.
     *
     * @param date Date to format.
     * @return Display text such as {@code Oct 15 2019}.
     */
    public static String formatDate(LocalDate date) {
        return date.format(DISPLAY_DATE);
    }

    /**
     * Returns this value as display text, with time only if one was given.
     *
     * @return Text such as {@code Dec 02 2019} or {@code Dec 02 2019, 6:00pm}.
     */
    public String toDisplayString() {
        if (hasTime) {
            return dateTime.format(DISPLAY_DATE_TIME).replace("AM", "am").replace("PM", "pm");
        }
        return dateTime.toLocalDate().format(DISPLAY_DATE);
    }

    /**
     * Returns this value encoded for the save file.
     *
     * @return An ISO date, or a date-time with {@code T} if a time was given.
     */
    public String toStorageString() {
        if (hasTime) {
            return dateTime.format(STORAGE_DATE_TIME);
        }
        return dateTime.toLocalDate().toString();
    }

    /**
     * Returns the calendar date of this value.
     *
     * @return The date, ignoring the clock time.
     */
    public LocalDate toLocalDate() {
        return dateTime.toLocalDate();
    }

    /**
     * Returns the stored date-time.
     *
     * @return Midnight if no time of day was specified.
     */
    public LocalDateTime toLocalDateTime() {
        return dateTime;
    }

    /**
     * Returns whether a time of day was specified.
     *
     * @return {@code true} if the original text included a time.
     */
    public boolean hasTime() {
        return hasTime;
    }

    /**
     * Returns whether {@code other} stores the same date, time, and time-of-day flag.
     *
     * @param other Value to compare.
     * @return {@code true} if both values would print and save the same way.
     */
    public boolean hasSameValue(TaskDateTime other) {
        return hasTime == other.hasTime && dateTime.equals(other.dateTime);
    }

    /**
     * Returns a parsed date-time from {@code text}, or {@code null} if no supported pattern matches.
     * Tries ISO date-times with {@code T}, then date-time patterns, then date-only patterns.
     *
     * @param text Raw date or date-time text.
     * @return The parsed value, or {@code null} if {@code text} cannot be parsed.
     */
    private static TaskDateTime tryParse(String text) {
        String trimmed = text.trim();
        if (trimmed.contains("T")) {
            return parseIsoDateTime(trimmed);
        }
        TaskDateTime withTime = parseUsingDateTimes(trimmed, DATE_TIME_FORMATTERS);
        if (withTime != null) {
            return withTime;
        }
        return parseUsingDates(trimmed, DATE_FORMATTERS);
    }

    /**
     * Returns an ISO date-time with {@code T}, or {@code null} if neither ISO form matches.
     *
     * @param text Trimmed text that contains {@code T}.
     * @return The parsed value, or {@code null}.
     */
    private static TaskDateTime parseIsoDateTime(String text) {
        try {
            return new TaskDateTime(LocalDateTime.parse(text), true);
        } catch (DateTimeParseException e) {
            // Try the save-file formatter next.
        }
        try {
            return new TaskDateTime(LocalDateTime.parse(text, STORAGE_DATE_TIME), true);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Returns a date-time parsed with the first matching formatter, or {@code null}.
     *
     * @param text Trimmed date-time text.
     * @param formatters Patterns to try, in order.
     * @return The parsed value, or {@code null} if none match.
     */
    private static TaskDateTime parseUsingDateTimes(String text, DateTimeFormatter... formatters) {
        for (DateTimeFormatter formatter : formatters) {
            try {
                return new TaskDateTime(LocalDateTime.parse(text, formatter), true);
            } catch (DateTimeParseException e) {
                // Try the next pattern.
            }
        }
        return null;
    }

    /**
     * Returns a date-only value parsed with the first matching formatter, or {@code null}.
     *
     * @param text Trimmed date text.
     * @param formatters Patterns to try, in order.
     * @return The parsed value at midnight, or {@code null} if none match.
     */
    private static TaskDateTime parseUsingDates(String text, DateTimeFormatter... formatters) {
        for (DateTimeFormatter formatter : formatters) {
            try {
                LocalDate date = LocalDate.parse(text, formatter);
                return new TaskDateTime(date.atStartOfDay(), false);
            } catch (DateTimeParseException e) {
                // Try the next pattern.
            }
        }
        return null;
    }
}
