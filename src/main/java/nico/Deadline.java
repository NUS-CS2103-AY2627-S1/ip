package nico;

import java.time.LocalDateTime;

/**
 * Represents a task that must be completed by a specific date and time.
 */
public class Deadline extends Task {
    private final LocalDateTime dueTime;

    /**
     * Creates a deadline task with its due date and time.
     *
     * @param description Task description.
     * @param dueTime Date and time the task is due.
     */
    public Deadline(String description, LocalDateTime dueTime) {
        super(description);
        this.dueTime = dueTime;
    }

    /**
     * Returns the deadline's due date and time for sorting and comparison.
     */
    public LocalDateTime getDueTime() {
        return dueTime;
    }

    /**
     * Returns this deadline in the format used for display and storage.
     *
     * @return Formatted deadline text, including its completion status and due time.
     */
    @Override
    public String toString() {
        String dueTimeString = dueTime.format(DATE_TIME_FORMATTER);
        return String.format("[D]%s %s (by: %s)", getStatusMarker(), getDescription(), dueTimeString);
    }
}
