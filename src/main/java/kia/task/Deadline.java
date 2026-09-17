package kia.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * A task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    /** Date by which the task should be completed. */
    protected LocalDate by;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description the task description
     * @param by the date by which the task should be completed
     */
    public Deadline(String description, LocalDate by) {
        super(description, TaskType.DEADLINE);
        assert by != null : "A deadline must have a parsed date.";
        this.by = by;
    }

    /**
     * Creates a deadline from an ISO-8601 date, retained for callers that use
     * the original string-based constructor.
     *
     * @param description the task description
     * @param by ISO date in {@code yyyy-MM-dd} format
     */
    public Deadline(String description, String by) {
        this(description, LocalDate.parse(by));
    }

    /**
     * Returns the deadline date.
     *
     * @return the date by which this task should be completed
     */
    public LocalDate getBy() {
        return by;
    }

    /**
     * Formats this deadline with its type, completion state, description, and date.
     *
     * @return the user-facing deadline representation
     */
    @Override
    public String toString() {
        return "[" + getTypeIcon() + "]" + super.toString()
                + " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }

    /**
     * Serializes this deadline with its ISO date for persistence.
     *
     * @return a pipe-delimited deadline record
     */
    @Override
    public String toStorageString() {
        return super.toStorageString() + " | " + by;
    }
}
