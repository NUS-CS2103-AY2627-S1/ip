package yachiyo.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a task that must be completed by a specific date and time.
 */
public class Deadline extends Task {
    private final LocalDateTime by;

    /**
     * Creates a deadline task with the given description and due date-time.
     *
     * @param description description of the task.
     * @param by date and time by which the task must be completed.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        assert by != null : "Deadline date-time must not be null";

        this.by = by;
    }

    /**
     * Returns the date and time by which this deadline must be completed.
     *
     * @return deadline date and time.
     */
    public LocalDateTime getBy() {
        return by;
    }

    /**
     * Returns a copy of this deadline with the specified description.
     *
     * @param description replacement description.
     * @return deadline containing the replacement description.
     */
    @Override
    public Deadline withDescription(String description) {
        return copyCompletionStatusTo(new Deadline(description, by));
    }

    /**
     * Returns a copy of this deadline with the specified due date-time.
     *
     * @param by replacement due date and time.
     * @return deadline containing the replacement due date-time.
     */
    public Deadline withBy(LocalDateTime by) {
        return copyCompletionStatusTo(new Deadline(getDescription(), by));
    }

    /**
     * Returns this deadline in the format used by the storage file.
     *
     * @return Stored deadline representation.
     */
    @Override
    public String toFileFormat() {
        return "DEADLINE | " + super.toFileFormat() + " | "
                + TaskDateTimeFormatter.formatForStorage(this.by);
    }

    /**
     * Returns whether this deadline falls on the specified date.
     *
     * @param date Date to check.
     * @return True if the deadline falls on the date; false otherwise.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return this.by.toLocalDate().equals(date);
    }

    /**
     * Returns this deadline in its user-facing display format.
     *
     * @return Formatted deadline description, status, and due date-time.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: "
                + TaskDateTimeFormatter.formatForDisplay(this.by) + ")";
    }
}
