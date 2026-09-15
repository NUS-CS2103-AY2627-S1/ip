package chatty.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specific date.
 */
public class Deadline extends Task {
    /** Format used to display deadline dates to the user. */
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    /** Date by which this task should be completed. */
    private final LocalDate dueDate;

    /**
     * Creates an incomplete deadline with the given description and due value.
     *
     * @param description description of the deadline.
     * @param dueDate date by which the task should be completed.
     */
    public Deadline(String description, LocalDate dueDate) {
        super(description);
        assert dueDate != null : "Deadline due date must not be null";
        this.dueDate = dueDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTypeIcon() {
        return "D";
    }

    /**
     * Returns the date by which this task should be completed.
     *
     * @return deadline due date.
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     * Returns this deadline with its formatted due date.
     *
     * @return formatted deadline information.
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + dueDate.format(DISPLAY_FORMAT) + ")";
    }
}
