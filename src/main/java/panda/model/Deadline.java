package panda.model;

import java.time.LocalDateTime;

import panda.util.DateTimeUtil;

/**
 * Represents a task that must be completed by a specified date/time.
 */
public class Deadline extends Task {
    private final LocalDateTime by;

    /**
     * Creates a deadline task with a parsed date/time value.
     *
     * @param description task description
     * @param by deadline date and time
     */
    public Deadline(String description, LocalDateTime by) {
        super(description, TaskType.DEADLINE);
        this.by = by;
    }

    /**
     * Creates a deadline task from a user-supplied date/time string.
     *
     * @param description task description
     * @param by deadline text to parse
     */
    public Deadline(String description, String by) {
        this(description, DateTimeUtil.parse(by));
    }

    /**
     * Returns the deadline date/time.
     *
     * @return deadline date/time
     */
    public LocalDateTime getBy() {
        return by;
    }

    @Override
    public boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other) && by.equals(((Deadline) other).by);
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + DateTimeUtil.formatForDisplay(by) + ")";
    }

    /**
     * Returns this deadline in the save-file format.
     *
     * @return one line of deadline data
     */
    @Override
    public String toFileString() {
        return super.toFileString() + " | " + DateTimeUtil.formatForStorage(by);
    }
}
