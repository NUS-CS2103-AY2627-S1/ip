package friday.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * A task that must be completed by a calendar date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);
    private LocalDate dueDate;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description Text describing the deadline.
     * @param dueDate Date by which the task should be completed.
     */
    public Deadline(String description, LocalDate dueDate) {
        super(description);
        this.dueDate = dueDate;
    }

    /**
     * Returns the date by which this task is due.
     *
     * @return The due date.
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     * Checks whether another deadline has the same description and due date.
     *
     * @param other Task to compare with this deadline.
     * @return Whether both deadlines have identical details.
     */
    @Override
    public boolean hasSameDetails(Task other) {
        return other instanceof Deadline deadline
                && super.hasSameDetails(other)
                && dueDate.equals(deadline.dueDate);
    }

    /**
     * Serializes this deadline for storage.
     *
     * @return A type marker followed by task fields and the ISO date.
     */
    @Override
    public String toFileString() {
        return "D | " + super.toFileString() + " | " + dueDate;
    }

    /**
     * Returns the user-facing deadline representation.
     *
     * @return The deadline type marker, status, description, and formatted date.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + dueDate.format(DISPLAY_FORMAT) + ")";
    }
}
