package swell.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Represents a date, with an optional time, used by deadline and event tasks.
 */
public class TaskDateTime {
    private static final DateTimeFormatter DATE_TIME_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm", Locale.ENGLISH);
    private static final DateTimeFormatter DATE_DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter TIME_DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);

    private final LocalDateTime value;
    private final boolean hasTime;

    private TaskDateTime(LocalDateTime value, boolean hasTime) {
        this.value = value;
        this.hasTime = hasTime;
    }

    /**
     * Creates a task date without a specific time.
     *
     * @param date date to store.
     * @return task date.
     */
    public static TaskDateTime of(LocalDate date) {
        return new TaskDateTime(date.atStartOfDay(), false);
    }

    /**
     * Creates a task date with a specific time.
     *
     * @param dateTime date and time to store.
     * @return task date and time.
     */
    public static TaskDateTime of(LocalDateTime dateTime) {
        return new TaskDateTime(dateTime, true);
    }

    /**
     * Parses a date or date-time from user input.
     *
     * @param text user input text.
     * @return parsed task date and optional time.
     * @throws DateTimeParseException if the text does not match an accepted date format.
     */
    public static TaskDateTime parse(String text) throws DateTimeParseException {
        String trimmedText = text.trim();
        try {
            return of(LocalDateTime.parse(trimmedText, DATE_TIME_INPUT_FORMAT));
        } catch (DateTimeParseException e) {
            return of(LocalDate.parse(trimmedText));
        }
    }

    /**
     * Returns the storage representation of this date or date-time.
     *
     * @return date or date-time text for saving.
     */
    public String toStorageString() {
        if (hasTime) {
            return value.format(DATE_TIME_INPUT_FORMAT);
        }
        return value.toLocalDate().toString();
    }

    /**
     * Returns the user-facing display representation of this date or date-time.
     *
     * @return formatted date or date-time text.
     */
    public String toDisplayString() {
        String dateText = value.format(DATE_DISPLAY_FORMAT);
        if (hasTime) {
            return dateText + " " + value.toLocalTime().format(TIME_DISPLAY_FORMAT);
        }
        return dateText;
    }

    /**
     * Returns whether this task date includes a specific time.
     *
     * @return true if a time is present.
     */
    public boolean hasTime() {
        return hasTime;
    }

    /**
     * Returns this date-time value.
     *
     * @return date-time value.
     */
    public LocalDateTime getValue() {
        return value;
    }
}
