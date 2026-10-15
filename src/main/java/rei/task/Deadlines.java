package rei.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specific date and time.
 */
public class Deadlines extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a", Locale.ENGLISH);
    private LocalDateTime by;

    /**
     * Creates an incomplete deadline with the specified due date and time.
     *
     * @param description Description of the deadline.
     * @param by Date and time by which the task is due.
     */
    public Deadlines(String description, LocalDateTime by) {
        super(description, TaskType.DEADLINE);
        this.by = by;
    }

    /** Returns the deadline's due date and time. */
    public LocalDateTime getBy() {
        return this.by;
    }

    /**
     * Updates the deadline's due date and time.
     *
     * @param by New due date and time.
     */
    public void setBy(LocalDateTime by) {
        this.by = by;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return super.toString() + " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }
}
