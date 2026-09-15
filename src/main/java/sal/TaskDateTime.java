package sal;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A date or date-time value with an explicit flag for whether a time was provided.
 * This avoids treating a midnight date-time as a date-only value.
 */
public class TaskDateTime {
    private final LocalDateTime value;
    private final boolean hasTime;

    /**
     * Creates a date-time that includes a specific time of day.
     *
     * @param value Date and time.
     */
    public TaskDateTime(LocalDateTime value) {
        this.value = value;
        this.hasTime = true;
    }

    /**
     * Creates a date-only value (no time of day).
     *
     * @param date Calendar date.
     */
    public TaskDateTime(LocalDate date) {
        this.value = date.atStartOfDay();
        this.hasTime = false;
    }

    /**
     * Returns the underlying date-time. For date-only values, the time is midnight.
     *
     * @return Stored date-time.
     */
    public LocalDateTime getValue() {
        return value;
    }

    /**
     * Returns whether this value includes an explicit time of day.
     *
     * @return {@code true} if a time was provided; {@code false} for date-only.
     */
    public boolean hasTime() {
        return hasTime;
    }
}
