package davidgoggins.task;

import java.time.LocalDate;

import davidgoggins.DavidGogginsException;

/** A task that must be finished by a given time. */
public class Deadline extends Task {
    /** A correct deadline command, suggested when the date cannot be read. */
    private static final String EXAMPLE = "deadline return book /by 2026-09-10";

    /**
     * When the task is due.
     *
     * <p>Held as a {@link LocalDate} rather than text, so a nonsense date is rejected
     * once here instead of being stored and printed back at the user unchanged.
     */
    private final LocalDate by;

    /**
     * Creates a deadline that is not done yet.
     *
     * @param description the task's text
     * @param by          when it is due, as {@code yyyy-mm-dd}, e.g. {@code 2023-01-30}
     * @throws DavidGogginsException if {@code by} is not a date in that format
     */
    public Deadline(String description, String by) throws DavidGogginsException {
        super(description);
        this.by = parseDate(by, EXAMPLE);
    }

    /** Shown as {@code [D][ ] return book (by: 2019-10-15)}. */
    @Override
    public String toString() {
        return "[D]" + getStatusIcon() + description + " (by: " + this.by + ")";
    }

    /** Saved as {@code D | 0 | return book | 2019-10-15}. */
    @Override
    public String toSaveFormat() {
        return toSaveFormat("D", by.toString());
    }

    /**
     * {@inheritDoc}
     *
     * <p>A deadline is only a duplicate if its date also matches.
     */
    @Override
    public boolean isDuplicateOf(Task other) {
        // super checks the class first, so the cast below is always safe.
        return super.isDuplicateOf(other) && by.equals(((Deadline) other).by);
    }
}
