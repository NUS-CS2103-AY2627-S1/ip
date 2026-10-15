package yourhand.tasks;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Stores a task date and an optional time while preserving whether a time was supplied.
 */
public class TaskDateTime {
    private static final DateTimeFormatter DATE_DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");
    private static final DateTimeFormatter DATE_TIME_DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy h:mma");

    private final LocalDateTime value;
    private final boolean hasTime;

    /**
     * Creates a date-only task value.
     *
     * @param date Date value.
     */
    public TaskDateTime(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("date value must be provided");
        }
        this.value = date.atStartOfDay();
        this.hasTime = false;
    }

    /**
     * Creates a task value with a supplied time.
     *
     * @param value Date and time value.
     */
    public TaskDateTime(LocalDateTime value) {
        if (value == null) {
            throw new IllegalArgumentException("date-time value must be provided");
        }
        this.value = value;
        this.hasTime = true;
    }

    /**
     * Returns the date-time value used for comparisons.
     *
     * @return Stored date and time.
     */
    public LocalDateTime getValue() {
        return value;
    }

    /**
     * Returns the machine-readable value used in the data file.
     *
     * @return ISO date or ISO date-time text.
     */
    public String toStorageString() {
        return hasTime ? value.toString() : value.toLocalDate().toString();
    }

    /**
     * Returns a user-friendly value for console output.
     *
     * @return Formatted date or date and time.
     */
    public String toDisplayString() {
        if (!hasTime) {
            return value.format(DATE_DISPLAY_FORMAT);
        }

        return formatDateTimeForDisplay();
    }

    /** Returns a date-time display value with consistent lower-case meridiem text. */
    private String formatDateTimeForDisplay() {
        return value.format(DATE_TIME_DISPLAY_FORMAT)
                .replace("AM", "am")
                .replace("PM", "pm");
    }
}
