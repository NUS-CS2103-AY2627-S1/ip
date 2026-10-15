package wwaffle.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy");

    private final LocalDate dueDate;

    /**
     * Creates an incomplete deadline with the given description and due time.
     *
     * @param description Description of the deadline.
     * @param by Due date in {@code yyyy-MM-dd} format.
     */
    public Deadline(String description, String by) {
        super(description);
        this.dueDate = LocalDate.parse(by);
    }

    /**
     * Returns the due date.
     *
     * @return Due date.
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     * Returns the deadline in its display format.
     *
     * @return Formatted deadline.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + dueDate.format(DISPLAY_FORMAT) + ")";
    }
}
