package kaya.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private final LocalDate by;

    /**
     * Creates a deadline with the given description and due date or time.
     *
     * @param description the description of the deadline
     * @param by the date by which the task must be completed
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the deadline text for saving to disk.
     *
     * @return the deadline date
     */
    public LocalDate getBy() {
        return by;
    }

    /** {@inheritDoc} */
    @Override
    public Task copy() {
        return copyStatusTo(new Deadline(getDescription(), by));
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }
}
