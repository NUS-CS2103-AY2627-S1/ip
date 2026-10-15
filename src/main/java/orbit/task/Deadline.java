package orbit.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that must be completed by a given date or time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy");

    private final LocalDate by;

    /**
     * Creates a new deadline task.
     *
     * @param description description of the task
     * @param by date by which the task must be completed
     */
    public Deadline(String description, LocalDate by) {
        super(description, TaskType.DEADLINE);
        this.by = by;
    }

    /**
     * Checks whether this deadline is due on a date.
     *
     * @param date date whose schedule is being viewed
     * @return true if the deadline is due on the date
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return by.equals(date);
    }

    /**
     * Returns the deadline task in its display format.
     *
     * @return the task type, status, description, and deadline
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }

    /**
     * Returns a representation of this deadline suitable for saving to a file.
     *
     * @return pipe-separated deadline data
     */
    @Override
    public String toDataString() {
        return super.toDataString() + " | " + by;
    }
}
