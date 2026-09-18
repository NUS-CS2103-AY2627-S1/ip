package sophon.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that should be completed by a specific date.
 */
public class Deadline extends Task {

    /** Date by which the task should be completed. */
    private final LocalDate by;

    /**
     * Creates a deadline with the given description and due date.
     *
     * @param description details of the deadline.
     * @param by when the deadline should be completed by.
     */
    public Deadline(String description, LocalDate by) {
        super(description, TaskType.DEADLINE);
        assert by != null : "Deadline date must not be null";

        this.by = by;
    }

    /**
     * Returns this deadline with its due date shown in a user-friendly format.
     *
     * @return display representation of this deadline.
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d yyyy");
        return super.toString() + " (by: " + by.format(formatter) + ")";
    }

    /**
     * Returns this deadline in the ISO date format used by the save file.
     *
     * @return save file representation of this deadline.
     */
    @Override
    public String toFileString() {
        return "D | " + (isDone ? "1" : "0") + " | " + description + " | " + by;
    }
}
