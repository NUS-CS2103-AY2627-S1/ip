package gepit.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that needs to be completed by a specific date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy");

    private final LocalDate due;

    /**
     * Creates a deadline with the specified description and due date.
     */
    public Deadline(String description, LocalDate due) {
        super(description);
        this.due = due;
    }

    public LocalDate getDue() {
        return due;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString()
                + " (by: " + due.format(DISPLAY_FORMAT) + ")";
    }

    @Override
    public String toDataString() {
        return "D | " + super.toDataString() + " | " + due;
    }
}
