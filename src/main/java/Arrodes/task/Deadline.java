package arrodes.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** A task that must be completed by a date, optionally including a time. */
public class Deadline extends Task {

    /** Date by which this task should be completed. */
    private final LocalDateTime dueBy;

    /**
     * Creates a deadline with a date and optional time.
     * @param description text describing the task
     * @param dueBy date and optional time by which the task is due
     * @throws IllegalArgumentException if {@code dueBy} is null
     */
    public Deadline(String description, LocalDateTime dueBy) {
        super(description);
        if (dueBy == null) {
            throw new IllegalArgumentException("Deadline date cannot be null.");
        }
        this.dueBy = dueBy;
    }

    /**
     * Returns the deadline's due date and time.
     *
     * @return due date and time
     */
    public LocalDateTime getDueBy() {
        return dueBy;
    }

    /**
     * Returns the task with a deadline prefix and formatted due date.
     * @return formatted deadline text
     */
    @Override
    public String toString() {
        return String.format("[D]%s (by: %s)", super.toString(), formatDueBy());
    }

    /**
     * Formats midnight as a date and other deadlines as date-times.
     * @return formatted due date
     */
    private String formatDueBy() {
        return dueBy.toLocalTime().equals(java.time.LocalTime.MIDNIGHT)
                ? dueBy.format(DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH))
                : dueBy.format(DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm", Locale.ENGLISH));
    }
}
