package joebiden.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/**
 * Represents a task that must be completed by a specific date and time.
 */
public class Deadline extends Task {

    private final LocalDateTime by;
    /**
     * Creates a deadline task with the given description and due date.
     *
     * @param name Description of the task.
     * @param by Due date and time of the task.
     */
    public Deadline(String name, LocalDateTime by) {
        super(name);
        this.by = by;
    }
    /**
     * Returns the due date and time of this deadline.
     *
     * @return Due date and time.
     */
    public LocalDateTime getBy() {
        return by;
    }
    /**
     * Returns a formatted string representation of this deadline.
     *
     * @return Formatted deadline string.
     */
    @Override
    public String toString() {
        DateTimeFormatter outputFormat =
                DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a");

        return "[D]" + super.toString()
                + " (by: " + by.format(outputFormat) + ")";
    }
}
