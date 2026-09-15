package tianyi.task;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * Represents a required task date with an optional time.
 */
public class TaskTime {
    private static final int DATE_TIME_PART_INDEX_DATE = 0;
    private static final int DATE_TIME_PART_INDEX_TIME = 1;
    private static final int DATE_TIME_PART_COUNT_DATE_ONLY = 1;
    private static final int DATE_TIME_PART_COUNT_MAXIMUM = 2;

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("d-M-uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm").withResolverStyle(ResolverStyle.STRICT);

    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("EEE, MMM dd uuuu", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_TIME_FORMAT =
            DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);

    private final LocalDate date;
    private final LocalTime time;
    private final boolean hasTime;

    /**
     * Parses a task date with an optional time.
     *
     * @param input Date in {@code d-M-yyyy} format, optionally followed by {@code HH:mm}.
     * @throws java.time.format.DateTimeParseException If the date or time is invalid.
     */
    public TaskTime(String input) {
        String[] dateTimeParts = input.trim().split("\\s+", DATE_TIME_PART_COUNT_MAXIMUM);
        date = LocalDate.parse(dateTimeParts[DATE_TIME_PART_INDEX_DATE], DATE_FORMAT);

        if (dateTimeParts.length == DATE_TIME_PART_COUNT_DATE_ONLY) {
            time = null;
            hasTime = false;
        } else {
            time = LocalTime.parse(dateTimeParts[DATE_TIME_PART_INDEX_TIME], TIME_FORMAT);
            hasTime = true;
        }

        assert hasTime == (time != null)
                : "hasTime should be true exactly when time is present";
    }

    /**
     * Reports whether this value includes a time of day.
     *
     * @return {@code true} if a time was supplied, otherwise {@code false}.
     */
    public boolean hasTime() {
        return hasTime;
    }

    /**
     * Compares this value's date with another value's date.
     *
     * @param time Value whose date is compared with this date.
     * @return {@code true} if this date is earlier than the other date.
     */
    public boolean isBefore(TaskTime time) {
        return date.isBefore(time.date);
    }

    /**
     * Compares this value's date with another value's date.
     *
     * @param time Value whose date is compared with this date.
     * @return {@code true} if this date is later than the other date.
     */
    public boolean isAfter(TaskTime time) {
        return date.isAfter(time.date);
    }

    /**
     * Reports whether this value is strictly earlier than another date and time.
     * Values on the same date are ordered only when both include a time.
     *
     * @param taskTime Value to compare against.
     * @return {@code true} if this value is strictly earlier, otherwise {@code false}.
     */
    public boolean isStrictlyBefore(TaskTime taskTime) {
        if (date.isBefore(taskTime.date)) {
            return true;
        }

        if (date.isAfter(taskTime.date)) {
            return false;
        }

        return hasTime && taskTime.hasTime && time.isBefore(taskTime.time);
    }

    /**
     * Formats this value in the machine-readable storage format.
     *
     * @return Date and optional time in their accepted input formats.
     */
    public String getData() {
        return hasTime
                ? date.format(DATE_FORMAT) + " " + time.format(TIME_FORMAT)
                : date.format(DATE_FORMAT);
    }

    /**
     * Formats this value as a human-readable English date and optional time.
     *
     * @return Display representation of this date and optional time.
     */
    @Override
    public String toString() {
        return hasTime
                ? date.format(DISPLAY_DATE_FORMAT) + ", " + time.format(DISPLAY_TIME_FORMAT)
                : date.format(DISPLAY_DATE_FORMAT);
    }
}
