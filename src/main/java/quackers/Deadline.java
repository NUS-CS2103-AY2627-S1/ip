package quackers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specified time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    private final LocalDate dueDate;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description the task description.
     * @param dueDate the deadline date.
     */
    public Deadline(String description, LocalDate dueDate) {
        super(TaskType.DEADLINE, description);
        this.dueDate = dueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     * Formats this deadline with its type, completion status, and due time.
     *
     * @return the formatted deadline
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + dueDate.format(DISPLAY_FORMAT) + ")";
    }
}
