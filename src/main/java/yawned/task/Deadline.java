package yawned.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specified time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern(
            "MMM dd uuuu HHmm", Locale.ENGLISH);
    private final LocalDateTime endDate;

    /**
     * Creates a deadline task with the given description and a deadline.
     *
     * @param description Description of the task.
     * @param endDate Deadline time.
     */
    public Deadline(String description, LocalDateTime endDate) {
        super(description);
        this.endDate = endDate;
    }

    /**
     * Returns the deadline time for storage.
     *
     * @return Deadline time.
     */
    public LocalDateTime getEndDate() {
        return endDate;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: %s)".formatted(
                endDate.format(DISPLAY_DATE_TIME_FORMAT).toUpperCase(Locale.ENGLISH));
    }
}
