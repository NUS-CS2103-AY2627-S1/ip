package bott.task;

import java.time.LocalDate;

/**
 * Represents a task that needs to be done before a specific date.
 */
public class Deadline extends Task {

    private final LocalDate by;

    /**
     * Creates a new deadline task.
     *
     * @param description Description of the task.
     * @param by Date by which the task should be done.
     */
    public Deadline(String description, LocalDate by) {
        super(description, TaskType.DEADLINE);
        this.by = by;
    }

    /**
     * Returns this task's textual representation, e.g.
     * "[D][ ] return book (by: Oct 15 2019)".
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + by.format(DISPLAY_DATE_FORMAT) + ")";
    }

    /**
     * Returns this task's representation for saving to file, e.g.
     * "D | 0 | return book | 2019-10-15".
     */
    @Override
    public String toFileFormat() {
        return super.toFileFormat() + " | " + by;
    }
}
