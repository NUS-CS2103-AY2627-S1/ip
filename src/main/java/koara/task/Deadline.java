package koara.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a particular date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private final LocalDate dueDate;

    /**
     * Creates an incomplete deadline.
     *
     * @param description Description of the deadline.
     * @param dueDate Date by which the task must be completed.
     */
    public Deadline(String description, LocalDate dueDate) {
        super(TaskType.DEADLINE, description, "");
        assert dueDate != null : "Deadline date must not be null";
        this.dueDate = dueDate;
    }

    /**
     * Returns this deadline with its due date in display format.
     *
     * @return Display representation of this deadline.
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + dueDate.format(DISPLAY_DATE_FORMAT) + ")";
    }

    /**
     * Returns this deadline in the line-based format used by the data file.
     *
     * @return Serialized deadline data.
     */
    @Override
    public String toDataString() {
        return super.toDataString() + " | " + dueDate;
    }
}
