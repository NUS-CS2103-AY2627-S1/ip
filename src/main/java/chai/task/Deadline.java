package chai.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** A task that must be completed by a specified date. */
public class Deadline extends Task {
    /** The human-friendly format used when displaying a deadline. */
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    /** The date by which this task should be completed. */
    protected LocalDate by;

    /**
     * Creates an incomplete deadline.
     *
     * @param description Text describing the deadline.
     * @param by Date by which the task should be completed.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        assert by != null : "Deadline date must not be null";
        this.by = by;
    }

    /**
     * Returns the deadline date.
     *
     * @return Date by which the task should be completed.
     */
    public LocalDate getBy() {
        return by;
    }

    @Override
    public LocalDate getSortDate() {
        return by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }
}
