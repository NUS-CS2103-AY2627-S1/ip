package lily.task;

import java.time.LocalDateTime;

import lily.parser.DateTimeParser;
import lily.storage.Storage;

/** Represents a task that must be completed by a specific date/time. */
public class Deadline extends Task {
    protected LocalDateTime by;
    private final boolean hasExplicitTime;

    /**
     * Creates a deadline with the given description and due time.
     *
     * @param description description of the task
     * @param by          date and time by which the task must be completed
     */
    public Deadline(String description, LocalDateTime by) {
        this(description, by, !by.toLocalTime().equals(java.time.LocalTime.MIDNIGHT));
    }

    /** Creates a deadline while retaining whether its time was explicitly entered. */
    public Deadline(String description, LocalDateTime by, boolean hasExplicitTime) {
        super(description);
        // Parsers and storage reconstruction must supply a concrete due time.
        assert by != null : "A deadline must have a due time";
        this.by = by;
        this.hasExplicitTime = hasExplicitTime;
    }

    /** Returns the date and time by which this task is due. */
    public LocalDateTime getBy() {
        return by;
    }

    /** Returns whether the user explicitly supplied a time for this deadline. */
    public boolean hasExplicitTime() {
        return hasExplicitTime;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + DateTimeParser.formatForDisplay(by, hasExplicitTime) + ")";
    }

    @Override
    public String toFileString() {
        return "D | " + (isDone ? "1" : "0") + " | " + Storage.escapeField(description)
                + " | " + DateTimeParser.formatForStorage(by) + " | " + hasExplicitTime;
    }
}
