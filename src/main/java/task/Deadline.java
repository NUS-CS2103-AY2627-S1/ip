package task;

/**
 * Represents a task that should be completed by a specified time.
 */
import michael.MichaelException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy HHmm");

    /** The date and time by which the task should be completed. */
    private final LocalDateTime by;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description the text describing the task
     * @param by the date and time by which the task should be completed
     * @throws MichaelException if the task description is empty
     */
    public Deadline(String description, LocalDateTime by) throws MichaelException {
        super(description, "deadline");
        assert by != null : "A deadline must have a date and time";
        this.by = by;
    }

    /**
     * Returns the task's deadline.
     *
     * @return the date and time by which the task should be completed
     */
    public LocalDateTime getBy() {
        return by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DISPLAY_FORMATTER) + ")";
    }
}
