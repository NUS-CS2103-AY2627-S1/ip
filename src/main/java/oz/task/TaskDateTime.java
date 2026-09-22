package oz.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import oz.exception.OzException;

/**
 * Represents a parsed date or date-time in the Oz chatbot.
 * Encapsulates java.time objects for formatting and persistence.
 */
public class TaskDateTime {
    /** Date format shared by task descriptions and date-filtered list headings. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    /** Time format omitting minutes for times on the hour. */
    private static final DateTimeFormatter DISPLAY_HOUR_FORMAT =
            DateTimeFormatter.ofPattern("ha", Locale.ENGLISH);

    /** Time format including minutes for times between whole hours. */
    private static final DateTimeFormatter DISPLAY_TIME_FORMAT =
            DateTimeFormatter.ofPattern("h:mma", Locale.ENGLISH);

    /** Formatter for saving date-only values to storage. */
    private static final DateTimeFormatter STORAGE_DATE_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd", Locale.ENGLISH);

    /** Formatter for saving date-time values to storage. */
    private static final DateTimeFormatter STORAGE_DATETIME_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm", Locale.ENGLISH);

    /** Parsed date-time object. */
    private final LocalDateTime dateTime;

    /** Whether the user specified a time component. */
    private final boolean hasTime;

    /**
     * Constructs a TaskDateTime with a LocalDateTime instance and time flag.
     *
     * @param dateTime The parsed LocalDateTime.
     * @param hasTime Whether the time component was explicitly provided.
     */
    private TaskDateTime(LocalDateTime dateTime, boolean hasTime) {
        assert dateTime != null : "The date-time value must be non-null";
        assert hasTime || dateTime.toLocalTime().equals(LocalTime.MIDNIGHT)
                  : "A date-only value must be stored at midnight";
        this.dateTime = dateTime;
        this.hasTime = hasTime;
    }

    /**
     * Creates a date-only value, using the start of the day for comparisons.
     *
     * @param date Date with no explicitly specified time.
     * @return Date-only task value.
     */
    public static TaskDateTime fromDate(LocalDate date) {
        assert date != null : "The date value must be non-null";
        return new TaskDateTime(date.atStartOfDay(), false);
    }

    /**
     * Creates a value with an explicitly specified time, including midnight.
     *
     * @param dateTime Date and time to retain for display, storage, and comparisons.
     * @return Task value with an explicit time component.
     */
    public static TaskDateTime fromDateTime(LocalDateTime dateTime) {
        assert dateTime != null : "The date-time value must be non-null";
        return new TaskDateTime(dateTime, true);
    }

    /**
     * Parses a raw date or date-time string into a TaskDateTime.
     *
     * @param input Raw date or date-time string.
     * @return A parsed TaskDateTime instance.
     * @throws OzException If the date/time string does not match any accepted format.
     */
    public static TaskDateTime parse(String input) throws OzException {
        return TaskDateTimeParser.parse(input);
    }

    /**
     * Parses a raw date string without accepting a time component.
     *
     * @param input Raw date string.
     * @return Parsed local date.
     * @throws OzException If the input is empty or does not match an accepted date format.
     */
    public static LocalDate parseDate(String input) throws OzException {
        return TaskDateTimeParser.parseDate(input);
    }

    /**
     * Parses a raw time string without accepting a date component.
     *
     * @param input Raw time string.
     * @return Parsed local time.
     * @throws OzException If the input is empty or does not match an accepted time format.
     */
    public static LocalTime parseTime(String input) throws OzException {
        return TaskDateTimeParser.parseTime(input);
    }

    /**
     * Produces a copy shifted by the requested number of days.
     *
     * @param days Number of days to add, which may be negative.
     * @return Shifted date-time retaining whether a time was explicitly provided.
     */
    public TaskDateTime plusDays(long days) {
        return new TaskDateTime(this.dateTime.plusDays(days), this.hasTime);
    }

    /**
     * Formats a date consistently with task descriptions.
     *
     * @param date Date to format.
     * @return Formatted date for user display.
     */
    public static String formatDate(LocalDate date) {
        assert date != null : "The display date must be non-null";
        return date.format(DISPLAY_DATE_FORMAT);
    }

    /**
     * Formats the date/time for user display.
     *
     * @return Formatted string for user viewing.
     */
    public String toDisplayString() {
        if (!this.hasTime) {
            return formatDate(this.dateTime.toLocalDate());
        }

        String timePart;
        if (this.dateTime.getMinute() == 0) {
            timePart = this.dateTime.format(DISPLAY_HOUR_FORMAT)
                    .toLowerCase(Locale.ENGLISH);
        } else {
            timePart = this.dateTime.format(DISPLAY_TIME_FORMAT)
                    .toLowerCase(Locale.ENGLISH);
        }
        return formatDate(this.dateTime.toLocalDate()) + ", " + timePart;
    }

    /**
     * Formats the date/time for disk storage.
     *
     * @return Formatted string for file persistence.
     */
    public String toStorageString() {
        if (this.hasTime) {
            return this.dateTime.format(STORAGE_DATETIME_FORMAT);
        }
        return this.dateTime.format(STORAGE_DATE_FORMAT);
    }

    /**
     * Checks if this date/time occurs after another date/time.
     *
     * @param other The other TaskDateTime to compare against.
     * @return True if this date/time is strictly after the other date/time.
     */
    public boolean isAfter(TaskDateTime other) {
        return this.dateTime.isAfter(other.dateTime);
    }

    /**
     * Checks if this date/time occurs before another date/time.
     *
     * @param other The other TaskDateTime to compare against.
     * @return True if this date/time is strictly before the other date/time.
     */
    public boolean isBefore(TaskDateTime other) {
        return this.dateTime.isBefore(other.dateTime);
    }

    /**
     * Extracts the LocalDate component of this date/time.
     *
     * @return The LocalDate representation.
     */
    public LocalDate toLocalDate() {
        return this.dateTime.toLocalDate();
    }

    @Override
    public String toString() {
        return toDisplayString();
    }
}
