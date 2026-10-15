package snoopy.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specific time.
 */
public class Deadline extends Task {
    /** Format used to present stored dates to the user. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private final LocalDate by;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description Text describing the task.
     * @param by Deadline date.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        assert by != null : "Deadline date must be provided";
        this.by = by;
    }

    /**
     * Converts this deadline into one line of saved data.
     *
     * @return Saved deadline data.
     */
    @Override
    public String toDataString() {
        return "D | " + super.toDataString() + " | " + this.by;
    }

    /**
     * Returns this task with its type and deadline.
     *
     * @return The formatted deadline task.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
