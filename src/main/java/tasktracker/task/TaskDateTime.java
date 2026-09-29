package tasktracker.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

import tasktracker.exception.TaskTrackerException;
import tasktracker.ui.Message;

/**
 * Represents a date and time wrapper for tasks, handling parsing,
 * UI display formatting, and file save formatting.
 */
public class TaskDateTime {
    private static final String DATE_TIME_PATTERN = "uuuu-MM-dd HHmm";
    private static final DateTimeFormatter STANDARD_FORMATTER =
            DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)
                    .withResolverStyle(ResolverStyle.STRICT);

    private static final DateTimeFormatter DISPLAY_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("MMM d yyyy, h:mm a")
            .toFormatter(Locale.ENGLISH);

    private final LocalDateTime dateTime;

    /**
     * Constructs a {@code TaskDateTime} instance by parsing a raw date-time string.
     * Supports CLI input format ({@code "yyyy-MM-dd HHmm"}).
     *
     * @param rawDateTime Raw date-time string to parse.
     * @throws TaskTrackerException If the input string is {@code null} or cannot be parsed.
     */
    public TaskDateTime(String rawDateTime) throws TaskTrackerException {
        if (rawDateTime == null) {
            throw new TaskTrackerException(Message.ERR_INVALID_DATE_TIME);
        }
        String trimmed = rawDateTime.trim();
        try {
            // Standard CLI input: 2026-12-31 2359
            this.dateTime = LocalDateTime.parse(trimmed, STANDARD_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new TaskTrackerException(Message.ERR_INVALID_DATE_TIME);
        }
    }

    /**
     * Retrieves the underlying {@code LocalDateTime} instance.
     *
     * @return The stored {@code LocalDateTime} object.
     */
    public LocalDateTime getDateTime() {
        return this.dateTime;
    }

    /**
     * Checks if this date-time occurs strictly after another date-time.
     *
     * @param other The other {@code TaskDateTime} to compare against.
     * @return {@code true} if this date-time is after the other date-time, {code @false} otherwise.
     */
    public boolean isAfter(TaskDateTime other) {
        return this.dateTime.isAfter(other.dateTime);
    }

    /**
     * Formats the date-time into a user-friendly display string (e.g., {@code "Dec 2 2019, 6:00 pm"}).
     *
     * @return Formatted date-time display string.
     */
    public String toDisplayString() {
        return this.dateTime.format(DISPLAY_FORMATTER);
    }

    /**
     * Formats the date-time into a standardized storage string for saving to a file.
     *
     * @return Standardized date-time string formatted for file persistence.
     */
    public String toFileString() {
        return this.dateTime.format(STANDARD_FORMATTER);
    }

    /**
     * Returns the formatted string representation of this date-time object.
     *
     * @return Formatted date-time string matching the display format.
     */
    @Override
    public String toString() {
        return toDisplayString();
    }
}
