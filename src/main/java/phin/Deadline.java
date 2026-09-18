package phin;

import java.time.LocalDate;

/**
 * Represents a task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    /**
     * Deadline date by which the task should be completed.
     */
    protected LocalDate by;

    /**
     * Creates a deadline task that has not been completed yet.
     *
     * @param description description of the task.
     * @param by deadline date in yyyy-MM-dd format.
     * @throws IllegalArgumentException if the date is invalid.
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = TaskDate.parse(by);
    }

    /**
     * Formats this task with its type indicator and deadline.
     *
     * @return the formatted deadline task.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + TaskDate.format(by) + ")";
    }
}
