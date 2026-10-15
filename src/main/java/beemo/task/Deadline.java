package beemo.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a given date or time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    private final LocalDate dueDate;

    /**
     * Creates an incomplete deadline with the specified description and date.
     *
     * @param description Description of the deadline.
     * @param dueDate Date by which the task must be completed.
     */
    public Deadline(String description, LocalDate dueDate) {
        super(description);
        assert dueDate != null : "Deadline date must not be null";
        this.dueDate = dueDate;
    }

    /**
     * Returns the date by which the task must be completed.
     *
     * @return Deadline date.
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     * Returns the deadline's type, status, description, and formatted date.
     *
     * @return Display form of the deadline.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + dueDate.format(OUTPUT_FORMAT) + ")";
    }
}
