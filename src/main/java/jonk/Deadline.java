package jonk;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that must be completed by a specific date.
 */
public class Deadline extends Task {

    /** Formats dates for user-facing task descriptions. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy");

    private final LocalDate dueDate;

    /**
     * Creates a deadline with the specified description and due date.
     *
     * @param description Description of the deadline.
     * @param dueDate Due date of the deadline in {@code yyyy-MM-dd} format.
     */
    public Deadline(String description, String dueDate) {
        super(description);
        this.dueDate = LocalDate.parse(dueDate);
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + dueDate.format(DISPLAY_DATE_FORMAT) + ")";
    }

    @Override
    public String toFileString() {
        return "D | " + super.toFileString() + " | " + encodeFileField(dueDate.toString());
    }
}
