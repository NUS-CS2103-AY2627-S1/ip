package bro.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

import bro.exception.BroException;

/**
 * Encapsulates date and time handling for tasks.
 * Supports parsing recognized date and date-time formats into java.time
 * objects,
 * while gracefully falling back to storing unparseable inputs as raw strings.
 */
public class TaskDateTime {
    private static final Pattern CALENDAR_DATE_LIKE = Pattern.compile(
            "^\\d{1,4}[-/]\\d{1,2}[-/]\\d{1,4}(\\s+.*)?$");

    private static final DateTimeFormatter[] DATE_TIME_FORMATTERS = {
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm").withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm").withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("uuuu-M-d HHmm").withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("d-M-uuuu HHmm").withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("d/M/uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm").withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("uuuu-M-d HH:mm").withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("d-M-uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm").withResolverStyle(ResolverStyle.STRICT)
    };

    private static final DateTimeFormatter[] DATE_FORMATTERS = {
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("uuuu-M-d").withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("d-M-uuuu").withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("uuuu/M/d").withResolverStyle(ResolverStyle.STRICT)
    };

    private static final DateTimeFormatter DISPLAY_DATE_TIME = DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma",
            Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private static final DateTimeFormatter FILE_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
    private static final DateTimeFormatter FILE_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final LocalDateTime dateTime;
    private final boolean hasTime;
    private final String rawString;

    /**
     * Constructs a TaskDateTime instance with a LocalDateTime and time flag.
     *
     * @param dateTime The LocalDateTime value.
     * @param hasTime  True if time of day was provided, false if date only.
     */
    public TaskDateTime(LocalDateTime dateTime, boolean hasTime) {
        this(dateTime, hasTime, null);
    }

    /**
     * Constructs a TaskDateTime instance with a raw string fallback.
     *
     * @param rawString The raw date/time string.
     */
    public TaskDateTime(String rawString) {
        this(null, false, rawString);
    }

    /**
     * Private full constructor for TaskDateTime.
     *
     * @param dateTime  The parsed LocalDateTime or null.
     * @param hasTime   True if time of day was provided.
     * @param rawString The raw string input or null.
     */
    private TaskDateTime(LocalDateTime dateTime, boolean hasTime, String rawString) {
        this.dateTime = dateTime;
        this.hasTime = hasTime;
        this.rawString = rawString;
    }

    /**
     * Parses a date or date-time string into a TaskDateTime object.
     * If the input matches a recognized format, it is parsed into a LocalDateTime.
     * If it looks like a calendar date but is invalid, an error is thrown.
     * Otherwise, it is preserved as a raw string so non-standard inputs (e.g.,
     * "2pm", "Sunday") continue to work.
     *
     * @param input The raw input string containing date or date-time information.
     * @return A parsed TaskDateTime instance.
     * @throws BroException If the input string is null, empty, or an invalid calendar date.
     */
    public static TaskDateTime parse(String input) throws BroException {
        if (input == null || input.trim().isEmpty()) {
            throw new BroException("Bro, you can't have an empty date.");
        }

        String trimmed = input.trim();

        // 1. Try parsing with date-time formatters
        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                LocalDateTime parsedDateTime = LocalDateTime.parse(trimmed, formatter);
                return new TaskDateTime(parsedDateTime, true, trimmed);
            } catch (DateTimeParseException ignored) {
                // Try next formatter
            }
        }

        // 2. Try parsing with date-only formatters
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                LocalDate parsedDate = LocalDate.parse(trimmed, formatter);
                return new TaskDateTime(parsedDate.atStartOfDay(), false, trimmed);
            } catch (DateTimeParseException ignored) {
                // Try next formatter
            }
        }

        // If input looks like a formatted date/time but failed strict parsing, it's invalid calendar date
        if (CALENDAR_DATE_LIKE.matcher(trimmed).matches()) {
            throw new BroException(String.format(
                    "Bro, '%s' isn't a real date on the calendar, check your date again.", trimmed));
        }

        // 3. Fallback: store as raw string if not matching any date/time pattern
        return new TaskDateTime(null, false, trimmed);
    }

    /**
     * Returns a user-friendly string representation of the date/time.
     * If parsed as a date/time object, returns formatted string (e.g., "Oct 15
     * 2019" or "Dec 02 2019, 6:00PM").
     * Otherwise, returns the raw input string (e.g., "2pm").
     *
     * @return Formatted date display string or raw string.
     */
    public String formatDisplay() {
        if (dateTime != null) {
            if (hasTime) {
                return dateTime.format(DISPLAY_DATE_TIME);
            }
            return dateTime.format(DISPLAY_DATE);
        }
        return rawString;
    }

    /**
     * Returns a standardized string representation of the date/time suitable for
     * file storage.
     * If parsed as a date/time object, returns standard format (e.g., "2019-10-15"
     * or "2019-12-02 1800").
     * Otherwise, returns the raw input string (e.g., "2pm").
     *
     * @return Formatted storage string or raw string.
     */
    public String formatFile() {
        if (dateTime != null) {
            if (hasTime) {
                return dateTime.format(FILE_DATE_TIME);
            }
            return dateTime.format(FILE_DATE);
        }
        return rawString;
    }

    /**
     * Gets the underlying LocalDateTime if parsed, or null if stored as a raw
     * string.
     *
     * @return The LocalDateTime instance, or null.
     */
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    /**
     * Returns whether this date-time object includes a specific time of day.
     *
     * @return True if time is included, false otherwise.
     */
    public boolean hasTime() {
        return hasTime;
    }

    /**
     * Returns whether the date/time was successfully parsed into a java.time
     * object.
     *
     * @return True if parsed as java.time object, false if stored as raw string.
     */
    public boolean isParsedDateTime() {
        return dateTime != null;
    }

    /**
     * Checks if this date/time object represents a date that matches the target
     * date.
     *
     * @param targetDate The LocalDate to check against.
     * @return True if this object has a parsed date and its date component equals
     *         targetDate, false otherwise.
     */
    public boolean isOnDate(LocalDate targetDate) {
        if (dateTime != null && targetDate != null) {
            return dateTime.toLocalDate().isEqual(targetDate);
        }
        return false;
    }

    /**
     * Returns the LocalDate representation if this object is a parsed date/time, or
     * null otherwise.
     *
     * @return The LocalDate instance, or null.
     */
    public LocalDate toLocalDate() {
        return (dateTime != null) ? dateTime.toLocalDate() : null;
    }

    /**
     * Parses a query date string into a LocalDate.
     *
     * @param input The input date string to search for (e.g., "27/8/2026" or
     *              "2026-08-27").
     * @return The parsed LocalDate.
     * @throws BroException If the input string cannot be parsed into a date.
     */
    public static LocalDate parseQueryDate(String input) throws BroException {
        if (input == null || input.trim().isEmpty()) {
            throw new BroException("Bro, please tell me which date you want to check (e.g. tasks 27/8/2026).");
        }

        String trimmed = input.trim();

        // Try date-only formatters
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(trimmed, formatter);
            } catch (DateTimeParseException ignored) {
                // Try next formatter
            }
        }

        // Try date-time formatters as well in case time was supplied
        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                return LocalDateTime.parse(trimmed, formatter).toLocalDate();
            } catch (DateTimeParseException ignored) {
                // Try next formatter
            }
        }

        if (CALENDAR_DATE_LIKE.matcher(trimmed).matches()) {
            throw new BroException(String.format(
                    "Bro, '%s' isn't a real date on the calendar, check your date again.", trimmed));
        }

        throw new BroException("Bro, please use a valid date format like 'd/M/yyyy' (e.g. 27/8/2026) "
                + "or 'yyyy-MM-dd' (e.g. 2026-08-27).");
    }

    /**
     * Checks if this TaskDateTime occurs before another TaskDateTime.
     * Only comparable if both instances have parsed date/times.
     *
     * @param other The other TaskDateTime to compare against.
     * @return True if this date/time is before the other, false otherwise.
     */
    public boolean isBefore(TaskDateTime other) {
        if (this.dateTime != null && other != null && other.dateTime != null) {
            return this.dateTime.isBefore(other.dateTime);
        }
        return false;
    }

    /**
     * Checks if this TaskDateTime occurs after another TaskDateTime.
     * Only comparable if both instances have parsed date/times.
     *
     * @param other The other TaskDateTime to compare against.
     * @return True if this date/time is after the other, false otherwise.
     */
    public boolean isAfter(TaskDateTime other) {
        if (this.dateTime != null && other != null && other.dateTime != null) {
            return this.dateTime.isAfter(other.dateTime);
        }
        return false;
    }

    /**
     * Gets the raw string input.
     *
     * @return The raw string representation.
     */
    public String getRawString() {
        return rawString;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TaskDateTime other)) {
            return false;
        }
        if (this.dateTime != null && other.dateTime != null) {
            return this.dateTime.isEqual(other.dateTime) && this.hasTime == other.hasTime;
        }
        if (this.dateTime == null && other.dateTime == null) {
            return Objects.equals(
                    this.rawString == null ? null : this.rawString.trim().toLowerCase(),
                    other.rawString == null ? null : other.rawString.trim().toLowerCase());
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (dateTime != null) {
            return Objects.hash(dateTime, hasTime);
        }
        return Objects.hash(rawString == null ? null : rawString.trim().toLowerCase());
    }
}
