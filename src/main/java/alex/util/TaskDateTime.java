package alex.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

/**
 * Represents a task date with an optional time.
 *
 * @param date task date.
 * @param time optional task time.
 */
public record TaskDateTime(LocalDate date, Optional<LocalTime> time) {
    /**
     * Creates a task date and validates its components.
     *
     * @param date task date.
     * @param time optional task time.
     */
    public TaskDateTime {
        assert date != null : "Task date cannot be null";
        assert time != null : "Optional task time cannot be null";
    }

    /**
     * Creates a task date without a time.
     *
     * @param date task date.
     */
    public TaskDateTime(LocalDate date) {
        this(date, Optional.empty());
    }

    /**
     * Creates a task date with a time.
     *
     * @param date task date.
     * @param time task time.
     */
    public TaskDateTime(LocalDate date, LocalTime time) {
        this(date, Optional.of(time));
    }
}
