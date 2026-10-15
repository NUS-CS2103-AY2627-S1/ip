package gunna.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents a deadline task with a specific due date.
 */
public class Deadline extends Task {
    public static final String TASK_TYPE = "D";
    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");
    protected LocalDate by;

    /**
     * Creates a Deadline task with a date.
     *
     * @param description The task description.
     * @param by The deadline date.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        assert by != null : "Deadline date cannot be null";
        this.by = by;
    }

    /**
     * Creates a Deadline task by parsing a date string.
     *
     * @param description The task description.
     * @param byStr The deadline date string in yyyy-MM-dd format.
     * @throws DateTimeParseException if the date string cannot be parsed.
     */
    public static Deadline createWithDateString(String description, String byStr) throws DateTimeParseException {
        LocalDate by = LocalDate.parse(byStr, INPUT_FORMAT);
        return new Deadline(description, by);
    }

    /**
     * Returns the deadline date as a LocalDate object.
     *
     * @return The deadline date.
     */
    public LocalDate getByDate() {
        return this.by;
    }

    /**
     * Returns the deadline date formatted for display.
     * Format: MMM dd yyyy (e.g., "Dec 25 2024")
     *
     * @return The formatted deadline date string.
     */
    public String getBy() {
        return this.by.format(OUTPUT_FORMAT);
    }

    /**
     * Returns the date in storage format (yyyy-MM-dd).
     *
     * @return The deadline date in yyyy-MM-dd format.
     */
    public String getByForStorage() {
        return this.by.format(INPUT_FORMAT);
    }

    /**
     * Converts the deadline task to a file-safe format for storage.
     * Format: D | status | description | date
     *
     * @return A pipe-separated string representation of the deadline task.
     */
    @Override
    public String toFileFormat() {
        return TASK_TYPE + FIELD_SEPARATOR + (isDone ? STATUS_DONE : STATUS_NOT_DONE)
                + FIELD_SEPARATOR + description + FIELD_SEPARATOR + by.format(INPUT_FORMAT);
    }

    /**
     * Returns a string representation of the deadline task for display.
     * Format: [D][status icon] description (by: formatted date)
     *
     * @return The string representation of the deadline task.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(OUTPUT_FORMAT) + ")";
    }
}
