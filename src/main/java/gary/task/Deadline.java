package gary.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that must be completed by a specific date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd");

    /** Date by which the task must be completed. */
    private final LocalDate by;

    /**
     * Creates an incomplete deadline.
     *
     * @param description Description of the task.
     * @param by Date by which the task must be completed.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        assert by != null : "Deadline date should not be null";

        this.by = by;
    }

    /**
     * Returns the deadline date.
     *
     * @return Deadline date.
     */
    public LocalDate getBy() {
        return by;
    }

    /**
     * Returns the deadline in a user-friendly display format.
     *
     * @return Formatted deadline.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }
}
