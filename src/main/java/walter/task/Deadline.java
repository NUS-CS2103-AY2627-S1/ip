package walter.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specific calendar date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    private final LocalDate dueDate;

    /**
     * Creates a deadline task with the given description and date.
     *
     * @param description Description of the deadline task.
     * @param dueDate Date when the task is due.
     */
    public Deadline(String description, LocalDate dueDate) {
        super(description);
        assert dueDate != null : "Deadline date should be validated before task creation";
        this.dueDate = dueDate;
    }

    /**
     * Returns the deadline date for persistence.
     *
     * @return Date when this task is due.
     */
    public LocalDate getBy() {
        return dueDate;
    }

    /**
     * Returns the Deadline type marker, task status, description, and friendly due date.
     *
     * @return Display form such as {@code [D][ ] submit report (by: Aug 30 2026)}.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + dueDate.format(DISPLAY_FORMATTER) + ")";
    }
}
