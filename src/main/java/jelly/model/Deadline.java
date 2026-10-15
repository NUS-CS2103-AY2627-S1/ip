package jelly.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm");

    /** The date or time by which the task should be completed. */
    private final LocalDateTime by;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description the text describing the deadline.
     * @param by the deadline date or time.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        if (by == null) {
            throw new IllegalArgumentException("Deadline date cannot be null.");
        }
        this.by = by;
    }

    /** Returns the deadline date or time. */
    public LocalDateTime getBy() {
        return this.by;
    }

    @Override
    protected String getTaskType() {
        return "D";
    }

    @Override
    protected String getTaskDetails() {
        return " (by: " + this.by.format(DISPLAY_FORMAT) + ")";
    }
}
