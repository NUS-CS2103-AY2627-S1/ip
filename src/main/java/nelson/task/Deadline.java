package nelson.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import nelson.exception.NelsonException;

/**
 * Represents a task that must be completed by a specified time.
 */
public class Deadline extends Task {
    /** The format used when displaying deadline dates to the user. */
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);
    /** The format used when writing deadline dates to storage. */
    private static final DateTimeFormatter STORAGE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");
    /** The deadline date. */
    protected LocalDate by;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description the text describing the task
     * @param by the deadline date in yyyy-mm-dd format
     * @throws NelsonException if the date is not in yyyy-mm-dd format
     */
    public Deadline(String description, String by) throws NelsonException {
        super(description);
        try {
            this.by = LocalDate.parse(by);
        } catch (DateTimeParseException exception) {
            throw new NelsonException("Molo! Invalid date. Please use the yyyy-mm-dd format.");
        }
    }

    /**
     * Returns this deadline in the format used for saving it to disk.
     *
     * @return the deadline type, status, description, and deadline separated by pipes
     */
    @Override
    public String toStorageString() {
        return "D | " + (isDone ? "1" : "0") + " | " + description + " | " + by.format(STORAGE_FORMAT);
    }

    /**
     * Returns the deadline type, status, description, and deadline.
     *
     * @return the formatted deadline task
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }
}
