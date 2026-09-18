package mochi.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    protected LocalDate by;

    /**
     * Creates an incomplete deadline.
     *
     * @param description description of the deadline
     * @param by deadline date
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    private Deadline(Deadline source) {
        super(source);
        this.by = source.by;
    }

    /** {@inheritDoc} */
    @Override
    public Deadline copy() {
        return new Deadline(this);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }

    /** {@inheritDoc} */
    @Override
    public String toFileString() {
        return "D" + STORAGE_SEPARATOR + super.toFileString() + STORAGE_SEPARATOR + by;
    }
}
