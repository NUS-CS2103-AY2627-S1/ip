package genie.model;

import java.time.LocalDateTime;

import genie.util.DateFormats;

/** Represents a task that needs to be done before a specific date or time. */
public class Deadline extends Task {
    protected LocalDateTime by;

    /**
     * Initializes a new Deadline task.
     *
     * @param description Description of the deadline.
     * @param by          The deadline date or time.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        assert by != null : "Deadline date and time must not be null";
        this.by = by;
    }

    /** Updates the deadline date and time. */
    public void setBy(LocalDateTime by) {
        assert by != null : "Deadline date and time must not be null";
        this.by = by;
    }

    /** Returns the deadline in the format used for file storage. */
    @Override
    public String toFileFormat() {
        return "D | " + super.toFileFormat() + " | " + by.format(DateFormats.INPUT_FORMAT);
    }

    /** Returns the deadline with its formatted due date. */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DateFormats.OUTPUT_FORMAT) + ")";
    }
}
