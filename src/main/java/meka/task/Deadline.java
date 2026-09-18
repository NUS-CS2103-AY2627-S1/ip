package meka.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specified date and time.
 */
public class Deadline extends Task {
    /** Format used to present a deadline date-time to the user. */
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu, h:mm a", Locale.ENGLISH);

    /** Date and time by which this task should be completed. */
    private final LocalDateTime by;

    /**
     * Creates a deadline task.
     *
     * @param description description of the task.
     * @param by date and time by which the task must be completed.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        assert by != null : "A deadline should have a due date and time";

        this.by = by;
    }

    /**
     * Returns a representation of this deadline suitable for saving to a file.
     *
     * The date-time uses the ISO local date-time format so it can be parsed
     * reliably when MEKA starts again.
     *
     * @return the task type, completion status, description, and ISO deadline.
     */
    @Override
    public String toDataString() {
        return formatDataString("D") + " | " + by;
    }

    /**
     * Returns the deadline with its type, completion status, and due date-time.
     *
     * @return the task formatted as
     *         "[D][ ] description (by: MMM dd yyyy, h:mm a)".
     */
    @Override
    public String toString() {
        return "[D]" + super.toString()
                + " (by: " + by.format(DISPLAY_DATE_TIME_FORMAT) + ")";
    }
}
