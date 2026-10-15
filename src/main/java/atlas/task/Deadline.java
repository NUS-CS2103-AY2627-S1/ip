package atlas.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** Represents a task that must be completed by a date. */
public class Deadline extends Task {
    private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");

    /** The date by which this task should be completed. */
    private final LocalDate by;

    /**
     * Creates a deadline with the given description and due date.
     *
     * @param description text describing the deadline.
     * @param by date by which the task should be completed.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the deadline date.
     *
     * @return date by which the task should be completed.
     */
    public LocalDate getBy() {
        return by;
    }

    /**
     * Returns this deadline in deadline display format.
     *
     * @return the deadline type marker, task details, and due date.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(OUTPUT_FORMAT) + ")";
    }
}
