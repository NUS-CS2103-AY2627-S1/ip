package penny.task;

import java.time.LocalDate;

import penny.common.PennyException;
import penny.common.Time;

/**
 * Represents a task that needs to be completed by a specific date and time.
 */
public class Deadline extends Task {

    /** Single-character type code identifying a deadline task in storage. */
    public static final String TYPE_CODE = "D";

    /** The date and optional time by which the deadline must be completed. */
    private final Time by;

    /**
     * Constructs a Deadline task with a description and a by-date string.
     *
     * @param description The deadline description.
     * @param by The deadline date/time string.
     * @throws PennyException If the date/time format is invalid.
     */
    public Deadline(String description, String by) throws PennyException {
        super(description);
        this.by = new Time(by);
    }

    /**
     * Constructs a Deadline task with a description and a pre-parsed Time object.
     *
     * @param description The deadline description.
     * @param by The pre-parsed Time object.
     */
    public Deadline(String description, Time by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the deadline Time instance.
     *
     * @return The {@link Time} by which the task must be completed.
     */
    public Time getBy() {
        return this.by;
    }

    /**
     * Checks if this deadline task is due on the specified date.
     *
     * @param targetDate The date to check against.
     * @return True if the deadline date matches targetDate, false otherwise.
     */
    @Override
    public boolean isOnDate(LocalDate targetDate) {
        return this.by.isOnDate(targetDate);
    }

    /**
     * Formats the deadline task into a pipe-delimited string for disk persistence.
     *
     * @return Formatted file storage string (e.g., "D | 0 | return book | 2019-10-15").
     */
    @Override
    public String toFileFormat() {
        return toFileFormatPrefix(TYPE_CODE) + FIELD_DELIMITER + this.by.toFileFormat();
    }

    /**
     * Returns the string representation of the deadline task for display.
     *
     * @return Formatted deadline string (e.g., "[D][ ] return book (by: Oct 15 2019)").
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + this.by.toString() + ")";
    }
}
