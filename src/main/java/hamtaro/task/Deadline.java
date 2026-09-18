package hamtaro.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** A task that must be completed by a specified time. */
public class Deadline extends Task {
    protected LocalDate by;

    /** Creates an incomplete deadline task. */
    public Deadline(String description, LocalDate by) {
        super(description);
        // A deadline without a date cannot be displayed or persisted correctly.
        assert by != null : "A deadline must have a date";
        this.by = by;
    }

    /** Creates a deadline task with its completion status restored from storage. */
    public Deadline(String description, LocalDate by, boolean isDone) {
        super(description, isDone);
        // Loaded records are expected to contain a parseable deadline date.
        assert by != null : "A deadline must have a date";
        this.by = by;
    }

    /** Returns the deadline text for persistent storage. */
    public LocalDate getBy() {
        return this.by;
    }

    /** Returns a displayable representation of this deadline task. */
    @Override
    public String toString() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd yyyy");
        return "[D]" + super.toString() + " (by: " + this.by.format(dtf) + ")";
    }
}
