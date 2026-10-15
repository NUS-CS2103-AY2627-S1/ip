package dingleberry.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that must be done before a specific date and time.
 */
public final class Deadlines extends Task {

    /** Stores the deadline date and time. */
    private final LocalDateTime by;

    /**
     * Creates an incomplete task due at the given date and time.
     *
     * @param description the user-visible task description.
     * @param deadline the deadline date and time.
     */
    public Deadlines(final String description, final LocalDateTime deadline) {
        super(description);
        this.by = deadline;
    }

    @Override
    public String toString() {
        return String.format("[D]%s (by: %s)", super.toString(),
            by.format(DISPLAY_DATE_TIME_FORMAT));
    }

    @Override
    public String toSaveFormat() {
        return "D | " + encodeCommonFields() + " | "
            + by.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
