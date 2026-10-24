package orbit.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that should be completed by a date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    private final LocalDate by;

    /**
     * Creates an incomplete deadline.
     *
     * @param description description shown to the user
     * @param by deadline date
     */
    public Deadline(String description, LocalDate by) {
        this(description, by, false);
    }

    /**
     * Creates a deadline with a stored completion status.
     *
     * @param description description shown to the user
     * @param by deadline date
     * @param isDone whether the deadline is completed
     */
    public Deadline(String description, LocalDate by, boolean isDone) {
        super(description, isDone);
        this.by = by;
    }

    /**
     * Returns the deadline date.
     *
     * @return deadline date
     */
    public LocalDate getBy() {
        return by;
    }

    /**
     * Formats this deadline using Orbit's user-facing date format.
     *
     * @return deadline marker, completion status, description, and due date
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }
}
