package ollie;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specific date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    private final LocalDate dueDate;

    /**
     * Creates a deadline with the given description and due date.
     *
     * @param description Description of the deadline.
     * @param dueDate Date by which the deadline should be completed.
     */
    public Deadline(String description, LocalDate dueDate) {
        super(description);
        this.dueDate = dueDate;
    }

    /**
     * Returns the due date of this deadline.
     *
     * @return Due date of this deadline.
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     * Returns whether another task has the same deadline details.
     *
     * @param other Task to compare with this deadline.
     * @return True if both deadlines have the same description and due date.
     */
    @Override
    public boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other)
                && dueDate.equals(((Deadline) other).dueDate);
    }

    /**
     * Returns a display-friendly representation of this deadline.
     *
     * @return Formatted deadline.
     */
    @Override
    public String toString() {
        return "[deadline]" + super.toString()
                + " (by: " + dueDate.format(DATE_FORMATTER) + ")";
    }
}
