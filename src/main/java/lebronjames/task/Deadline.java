package lebronjames.task;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    /** Letter used to identify a deadline in the save file. */
    public static final String FILE_TYPE = "D";

    private final TaskDateTime by;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description Description of the task.
     * @param by Date, and optionally time, by which the task must be completed.
     */
    public Deadline(String description, TaskDateTime by) {
        super(description);
        this.by = by;
    }

    @Override
    public boolean isOn(LocalDate date) {
        return by.getDate().equals(date);
    }

    @Override
    public String toFileFormat() {
        return FILE_TYPE + " | " + getDoneFlag() + " | " + getDescription() + " | " + by.toFileFormat();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Two deadlines with the same description but different due dates are
     * different tasks, so the date is compared as well. "submit report /by
     * 2019-12-02" and "submit report /by 2019-12-09" can both be on the list.
     */
    @Override
    public boolean equals(Object other) {
        if (!super.equals(other)) {
            return false;
        }

        // super.equals has already established that other is a Deadline.
        Deadline otherDeadline = (Deadline) other;
        return by.equals(otherDeadline.by);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), by);
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}
