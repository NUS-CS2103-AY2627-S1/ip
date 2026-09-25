package goat;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Represents a task that must be completed by a specified date.
 */
public class Deadline extends Task {
    /** Due date of this deadline. */
    protected LocalDate dueDate;

    /**
     * Creates an incomplete deadline with the given description and due date.
     *
     * @param description description of the deadline
     * @param by date by which the task should be completed
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.dueDate = by;
    }

    /**
     * Returns this deadline in the data-file format.
     *
     * @return serialized deadline data
     */
    @Override
    public String toFileString() {
        return formatFileData("D", dueDate.toString());
    }

    /**
     * Uses the due date when sorting deadlines chronologically.
     *
     * @return deadline due date
     */
    @Override
    public Optional<LocalDate> getSchedulingDate() {
        return Optional.of(dueDate);
    }

    /**
     * Returns the deadline with its type, completion status, and due date.
     *
     * @return formatted deadline
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + TaskDate.format(dueDate) + ")";
    }
}
