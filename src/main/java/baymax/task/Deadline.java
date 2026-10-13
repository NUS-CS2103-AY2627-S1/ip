package baymax.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that must be completed before a specified date.
 */
public class Deadline extends Task {

    /** The date by which the task should be completed. */
    private final LocalDate dueDate;

    /**
     * Creates a new unfinished deadline task.
     *
     * @param description the text describing the task
     * @param dueDate the date by which the task should be completed
     */
    public Deadline(String description, LocalDate dueDate) {
        super(description);
        assert dueDate != null : "Deadline dates should be parsed before construction.";
        this.dueDate = TaskDate.validate(dueDate);
    }

    /**
     * Formats the deadline date for user-facing display.
     *
     * @return the due date in {@code MMM dd yyyy} format
     */
    private String formatDueDate() {
        return dueDate.format(DateTimeFormatter.ofPattern("MMM dd yyyy"));
    }

    /**
     * Returns the deadline type marker, task details, and due date.
     *
     * @return the formatted deadline task
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + formatDueDate() + ")";
    }

    /**
     * Returns the deadline fields in the format used when saving tasks.
     *
     * @return the task type, completion status, description, and due date
     */
    @Override
    public String toStorageString() {
        return "D" + " | " + super.toStorageString() + " | " + dueDate;
    }
}
