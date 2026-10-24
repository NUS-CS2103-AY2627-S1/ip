package kiki.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that must be done by a specific date/time.
 */
public class Deadlines extends Task {
    private final LocalDateTime by;

    private static final DateTimeFormatter OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a");

    /**
     * Creates a new deadline task.
     *
     * @param description Description of the task.
     * @param by Date/time by which the task must be done.
     */
    public Deadlines(String description, LocalDateTime by) {
        super(description, TaskType.DEADLINE);
        this.by = by;
    }

    /**
     * Returns the deadline date and time.
     *
     * @return Deadline date and time.
     */
    public LocalDateTime getBy() {
        return by;
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + by.format(OUTPUT_FORMAT) + ")";
    }
}
