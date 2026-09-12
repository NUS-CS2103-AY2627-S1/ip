package bob.task;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

/**
 * Represents a task date with an optional time of day.
 */
public class TaskDateTime {

    private final LocalDate date;
    private final LocalTime time;

    /**
     * Creates a task date-time.
     *
     * @param date task date.
     * @param time optional time of day; null represents a date without a time.
     */
    public TaskDateTime(LocalDate date, LocalTime time) {
        assert date != null : "A task date-time must have a date";
        this.date = date;
        this.time = time;
    }

    /**
     * Returns the task date.
     *
     * @return task date.
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns the time of day when one was specified.
     *
     * @return optional task time.
     */
    public Optional<LocalTime> getTime() {
        return Optional.ofNullable(time);
    }
}
