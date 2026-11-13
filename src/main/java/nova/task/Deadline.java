package nova.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private final LocalDate dueDate;

    /**
     * Creates a deadline task.
     *
     * @param description Description of the task.
     * @param dueDate Due date.
     */
    public Deadline(String description, LocalDate dueDate) {
        super(description);
        this.dueDate = dueDate;
    }

    @Override
    protected String getTypeIcon() {
        return "D";
    }

    @Override
    public String toDataString() {
        return "D | " + (isDone() ? "1" : "0") + " | " + getDescription() + " | " + dueDate;
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + dueDate.format(OUTPUT_FORMAT) + ")";
    }
}
