package yuki.task;

import java.util.Objects;

import yuki.time.DateTimeParser;
import yuki.time.TaskDateTime;

/**
 * Represents a task that must be completed by a specific date and time.
 */
public class Deadline extends Task {
    /** The date and time by which this task should be completed. */
    private final TaskDateTime by;

    /**
     * Creates a deadline task.
     *
     * @param description The text describing the task.
     * @param by The date and time by which the task should be completed.
     */
    public Deadline(String description, TaskDateTime by) {
        super(description);
        this.by = Objects.requireNonNull(by, "deadline must not be null");
    }

    /**
     * Returns the date and time by which this task should be completed.
     *
     * @return This deadline's due date and time.
     */
    public TaskDateTime getBy() {
        return by;
    }

    /** Returns whether another deadline has the same description and due date. */
    @Override
    public boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other)
                && by.hasSameValueAs(((Deadline) other).by);
    }

    /**
     * Returns this deadline task in the format used in the task list.
     *
     * @return The formatted deadline task.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + DateTimeParser.format(by) + ")";
    }
}
