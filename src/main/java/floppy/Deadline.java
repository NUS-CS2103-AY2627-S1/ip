package floppy;

import java.time.LocalDate;

/**
 * Represents a task that must be completed by a given time.
 */
public class Deadline extends Task {
    private final LocalDate dueDate;

    /**
     * Creates a deadline with the specified description and due date.
     *
     * @param description Description of the deadline.
     * @param dueDate Due date of the deadline.
     */
    public Deadline(String description, LocalDate dueDate) {
        super(description, TaskType.DEADLINE);
        this.dueDate = dueDate;
    }

    /**
     * Returns the due date.
     *
     * @return Due date of this deadline.
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + dueDate.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
