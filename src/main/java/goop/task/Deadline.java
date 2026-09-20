package goop.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specific date or time.
 */
public class Deadline extends Task {
    /** Format used when displaying deadlines to the user. */
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d uuuu, h:mm a", Locale.ENGLISH);

    private final LocalDateTime by;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description Text describing the task.
     * @param by Date and time by which the task must be completed.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the deadline date and time stored for this task.
     *
     * @return Deadline date and time.
     */
    public LocalDateTime getBy() {
        return by;
    }

    /**
     * Returns this task with its deadline type icon and deadline text.
     *
     * @return The formatted deadline task.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }
}
