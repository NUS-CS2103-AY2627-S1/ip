package baby;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/**
 * Represents a task that should be completed by a specific date or time.
 */

public class Deadline extends Task {
    private LocalDateTime by;

    /**
     * Creates a deadline task with the given description and deadline.
     *
     * @param description Description of the deadline task.
     * @param by Deadline text.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    @Override
    public String getTypeIcon() {
        return "D";
    }

    @Override
    public String toFileString() {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

        return "D | "
                + (isDone ? "1" : "0")
                + " | "
                + description
                + " | "
                + by.format(formatter);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a");

        return super.toString() + " (by: " + by.format(formatter) + ")";
    }
}
