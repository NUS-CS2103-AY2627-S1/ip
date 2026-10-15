package jeff.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that must be completed by a specific date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy");

    private final LocalDate dueDate;

    /**
     * Creates a deadline with the specified description and due date.
     *
     * @param description Description of the deadline.
     * @param dueDate Due date of the deadline.
     */
    public Deadline(String description, LocalDate dueDate) {
        super(description);
        this.dueDate = dueDate;
    }

    /**
     * Returns the deadline's due date.
     *
     * @return Due date of the deadline.
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString()
                + " (by: " + dueDate.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
