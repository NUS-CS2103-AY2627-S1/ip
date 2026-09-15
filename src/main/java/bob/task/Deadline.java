package bob.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that must be completed by a specified deadline.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DATE_FORMATTER_FOR_STORAGE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DATE_FORMATTER_FOR_DISPLAY =
            DateTimeFormatter.ofPattern("MMM dd yyyy");

    private final LocalDate dueDate;

    /**
     * Creates a deadline task.
     *
     * @param description Description of the task.
     * @param dueDate Date by which the task should be completed.
     */
    public Deadline(String description, LocalDate dueDate) {
        super(description);
        this.dueDate = dueDate;
    }

    /**
     * Returns the date by which this task should be completed.
     *
     * @return Due date of this deadline.
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     * Returns the deadline task representation used for data storage.
     *
     * @return Data representation containing the task type, status, description, and deadline.
     */
    @Override
    public String toDataString() {
        return "D | " + (isDone() ? "1" : "0")
                + " | " + this.description
                + " | " + dueDate.format(DATE_FORMATTER_FOR_STORAGE);
    }

    /**
     * Returns a display representation containing the task details and formatted deadline.
     *
     * @return Display representation of this deadline task.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString()
                + " (by: " + dueDate.format(DATE_FORMATTER_FOR_DISPLAY) + ")";
    }
}
