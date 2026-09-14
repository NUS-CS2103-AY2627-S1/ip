package johnny.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that needs to be done before a specific date.
 * The date is stored as a LocalDate and displayed as MMM dd yyyy.
 */
public class Deadline extends Task {

    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy");

    private final LocalDate dueDate;

    /**
     * Creates a deadline task with the given description and due date.
     *
     * @param description the task description
     * @param dueDate the due date
     */
    public Deadline(String description, LocalDate dueDate) {
        super(description);
        this.dueDate = dueDate;
    }

    /** Returns file format: {@code D | done | description | yyyy-MM-dd}. */
    @Override
    public String toFileString() {
        return "D | " + super.toFileString() + " | " + dueDate;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + dueDate.format(DISPLAY_FORMAT) + ")";
    }
}
