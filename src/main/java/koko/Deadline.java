package koko;

import java.time.LocalDateTime;

/**
 * Represents a task that must be completed by a specified time.
 */
public class Deadline extends Task {
    /** The deadline date and time. */
    private final LocalDateTime by;

    /**
     * Creates a deadline task.
     *
     * @param description the text describing the task
     * @param by the deadline date and time
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        assert by != null : "A deadline must have a due date and time.";
        this.by = by;
    }

    /**
     * Returns the deadline date and time.
     *
     * @return the deadline date and time
     */
    public LocalDateTime getBy() {
        return by;
    }

    /**
     * Returns this deadline in the task-list format.
     *
     * @return the formatted deadline
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DISPLAY_DATE_TIME_FORMAT) + ")";
    }
}
