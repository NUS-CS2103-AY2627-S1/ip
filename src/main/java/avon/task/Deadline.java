package avon.task;

import java.time.LocalDateTime;
import java.util.Objects;

import avon.util.DateTimeParser;

/**
 * Represents a task that must be completed by a specified date and optional time.
 */
public class Deadline extends Task {
    private final LocalDateTime by;

    /**
     * Creates an unfinished deadline task.
     *
     * @param description the text describing the task.
     * @param by the date and time by which the task should be completed.
     * @throws NullPointerException if the deadline date-time is null.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = Objects.requireNonNull(by, "The deadline date-time cannot be null.");
    }

    /**
     * Returns the date and time by which this task should be completed.
     *
     * @return the deadline date and time.
     */
    public LocalDateTime getBy() {
        return by;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasSameDetailsAs(Task other) {
        return super.hasSameDetailsAs(other)
                && by.equals(((Deadline) other).by);
    }

    /**
     * Returns this deadline with its type, completion status, and due date.
     *
     * @return the formatted deadline task.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + DateTimeParser.format(by) + ")";
    }
}
