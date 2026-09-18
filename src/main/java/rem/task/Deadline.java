package rem.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A task that must be completed by a specific date and time.
 */
public class Deadline extends Task {
    protected LocalDateTime by;

    /**
     * Creates a deadline.
     *
     * @param description Description of the task.
     * @param by Date and time by which the task must be completed.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        assert by != null : "Deadline due date must not be null";
        this.by = by;
    }

    /**
     * Returns the date and time by which this task is due.
     *
     * @return Due date and time.
     */
    public LocalDateTime getBy() {
        return by;
    }

    /**
     * Checks whether this deadline is due on a given date.
     *
     * @param date Date to check.
     * @return True if the deadline is due on that date.
     */
    public boolean occursOn(LocalDate date) {
        return by.toLocalDate().equals(date);
    }

    /**
     * Returns this deadline's completion status, description, due date, and optional note.
     *
     * @return Display representation of this deadline.
     */
    @Override
    public String toString() {
        return formatWithNote("[D]" + getTaskDetails()
                + " (by: " + TaskDateTime.format(by) + ")");
    }
}
