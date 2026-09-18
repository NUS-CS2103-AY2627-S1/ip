package toothless.task;

import java.time.LocalDate;

/**
 * Represents a task that must be completed by a specified date.
 */
public class Deadline extends Task {
    private final LocalDate by;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description description of the deadline.
     * @param by date by which the task should be completed.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the date by which this deadline should be completed.
     *
     * @return deadline date
     */
    public LocalDate getBy() {
        return by;
    }

    /**
     * Returns this deadline in the chatbot's display format.
     *
     * @return formatted deadline
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + DeadlineDate.format(by) + ")";
    }
}
