package nudge.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specified date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private final LocalDate dueDate;

    /**
     * Creates an incomplete deadline with the specified description and due date.
     *
     * @param description description of the deadline.
     * @param dueDate date by which the deadline must be completed.
     */
    public Deadline(String description, LocalDate dueDate) {
        super(description);
        assert dueDate != null : "Deadline due date should not be null";
        this.dueDate = dueDate;
    }

    /**
     * Returns the deadline's due date.
     *
     * @return due date.
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     * Returns the deadline formatted with its task type, status, and due date.
     *
     * @return formatted deadline.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + dueDate.format(DISPLAY_FORMATTER) + ")";
    }
}
