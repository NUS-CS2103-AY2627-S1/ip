package lumi.task;

import java.time.LocalDateTime;

import lumi.datetime.DateTimeParser;

/**
 * Represents a task that must be completed by a particular date or time.
 */
public class Deadline extends Task {
    /** The date or time by which the task must be completed. */
    private LocalDateTime by;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description Description of the task.
     * @param by Date or time by which the task must be completed.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the deadline's due-date details for storage and other non-UI uses.
     *
     * @return The date or time by which the task must be completed.
     */
    public LocalDateTime getBy() {
        return by;
    }

    /**
     * Moves this deadline to a new due date or time.
     *
     * @param newDueDate New date or time by which the task must be completed.
     */
    public void reschedule(LocalDateTime newDueDate) {
        by = newDueDate;
    }

    /**
     * Returns the deadline in the format used when displaying task lists.
     *
     * @return The task type, status, description, and deadline.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + DateTimeParser.format(by) + ")";
    }
}
