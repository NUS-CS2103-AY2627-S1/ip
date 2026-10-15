package esther.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a deadline.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private final LocalDate dueDate;

    /**
     * Creates a deadline with the specified description and due date.
     *
     * @param description Description of the deadline.
     * @param dueDate Due date of the deadline.
     */
    public Deadline(String description, LocalDate dueDate) {
        super(description, TaskType.DEADLINE);
        this.dueDate = dueDate;
    }

    @Override
    public String toDataString() {
        return super.toDataString() + " | " + dueDate;
    }

    @Override
    public String toString() {
        return String.format(
                "%s (by: %s)",
                super.toString(),
                dueDate.format(DISPLAY_DATE_FORMAT)
        );
    }
}
