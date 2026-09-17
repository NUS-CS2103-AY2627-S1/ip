package panda.task;

import java.time.LocalDate;
import java.time.temporal.Temporal;

import panda.util.datetime.DateTimeHelper;

/**
 * Represents a task that must be completed by a supplied date.
 */
public class Deadline extends Task {
    private final Temporal dueDate;

    /**
     * Creates an unmarked deadline task.
     *
     * @param description Description of the task.
     * @param dueDate Date by which the task is due.
     */
    public Deadline(String description, Temporal dueDate) {
        super(description);
        assert dueDate != null : "Deadline due date must not be null";
        this.dueDate = dueDate;
    }

    /**
     * Returns the deadline date.
     *
     * @return Deadline date as a {@link Temporal}.
     */
    public Temporal getDueDate() {
        return dueDate;
    }

    /**
     * Checks if the deadline falls on the specified date.
     *
     * @param date The date to check against.
     * @return Whether the due date falls on the specified date.
     */
    @Override
    public boolean occursOn(Temporal date) {
        if (date == null) {
            return false;
        }
        LocalDate targetDate = LocalDate.from(date);
        LocalDate dueDateLocal = LocalDate.from(this.dueDate);
        return dueDateLocal.equals(targetDate);
    }

    /**
     * Returns a string representation of the deadline.
     *
     * @return Formatted deadline string.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + DateTimeHelper.formatForDisplay(dueDate) + ")";
    }
}
