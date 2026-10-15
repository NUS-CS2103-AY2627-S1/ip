package shrekanddonkey.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter OUTPUT_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a", Locale.ENGLISH);
    /** Deadline date and time. */
    private LocalDateTime by;

    /**
     * Creates a deadline that is initially not done.
     *
     * @param description description of the task.
     * @param by deadline date and time.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        assert by != null : "Deadline date/time cannot be null";
        this.by = by;
    }

    /**
     * Returns the deadline date and time.
     *
     * @return deadline date and time.
     */
    public LocalDateTime getBy() {
        return by;
    }

    /**
     * Returns this task with its deadline type marker and deadline text.
     *
     * @return formatted deadline text.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(OUTPUT_FORMATTER) + ")";
    }
}
