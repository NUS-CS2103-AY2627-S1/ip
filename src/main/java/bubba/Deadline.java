package bubba;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that is due at a specified date and time.
 */
public class Deadline extends Task {
    /** Due date and time of the task. */
    protected LocalDateTime by;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description Description of the task.
     * @param by Due date and time.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description, TaskType.DEADLINE);
        this.by = by;
    }

    /**
     * Returns the task summary with its due date formatted as {@code MMM dd yyyy HH:mm}.
     *
     * @return Human-readable deadline summary.
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm");

        return super.toString() + " (by: " + by.format(formatter) + ")";
    }

    /**
     * Returns the task record with its due date formatted as {@code dd/MM/yyyy HHmm}.
     *
     * @return Pipe-separated deadline record.
     */
    @Override
    public String toStorageString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HHmm");
        return super.toStorageString() + " | " + by.format(formatter);
    }
}
