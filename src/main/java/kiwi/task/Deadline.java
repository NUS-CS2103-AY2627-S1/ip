package kiwi.task;

import java.time.LocalDate;

/**
 * A deadline task that must be done before a given calendar date.
 */
public class Deadline extends Task {
    private final LocalDate by;

    /**
     * Creates a deadline task.
     *
     * @param description what needs to be done.
     * @param by due date.
     */
    public Deadline(String description, LocalDate by) {
        super(description, TaskType.DEADLINE);
        assert by != null : "Deadline date should already be parsed";
        this.by = by;
    }

    /**
     * Returns the due date.
     *
     * @return due date.
     */
    public LocalDate getBy() {
        return by;
    }

    /**
     * {@inheritDoc}
     * Also requires the same due date.
     */
    @Override
    public boolean hasSameDetails(Task other) {
        if (!super.hasSameDetails(other)) {
            return false;
        }
        Deadline deadline = (Deadline) other;
        return by.equals(deadline.by);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@code true} if {@code date} is this deadline's due date.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return by.equals(date);
    }

    /**
     * {@inheritDoc}
     * Appends the due date in {@code yyyy-MM-dd} form so it can be loaded again.
     */
    @Override
    public String toSaveFormat() {
        // Keep ISO on disk so load can parse reliably.
        return super.toSaveFormat() + SAVE_FIELD_SEPARATOR + by.format(KiwiDate.INPUT_FORMAT);
    }

    /**
     * {@inheritDoc}
     * Appends {@code (by: MMM dd yyyy)} for display.
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + KiwiDate.format(by) + ")";
    }
}
