package anaconda.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specified time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private final LocalDate by;

    /**
     * Creates a Deadline with its description and deadline date.
     *
     * @param description Description of the task.
     * @param by Deadline date.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
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

    @Override
    public LocalDate getEndDate() {
        return by;
    }

    /**
     * Formats the deadline using its type, completion status, description, and deadline date.
     *
     * @return Display representation of this deadline.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DISPLAY_FORMATTER) + ")";
    }
}
