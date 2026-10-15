package es;

import java.time.LocalDateTime;

/**
 * Represents a task that must be completed by a specified time.
 */
public class Deadline extends Task {
    private final LocalDateTime by;

    /**
     * Creates a deadline task.
     *
     * @param description the text describing the task
     * @param by the deadline text entered by the user
     */
    public Deadline(String description, String by) throws EsException {
        super(description, TaskType.DEADLINE);
        this.by = DateTimeParser.parse(by);
    }

    /**
     * Returns the encoded deadline line for saving to disk.
     *
     * @return a line in the form {@code D | 0 | description | by}
     */
    @Override
    public String toStorageLine() {
        return toStoragePrefix() + " | " + by;
    }

    /**
     * Returns the formatted deadline task.
     *
     * @return the task with its deadline
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + DateTimeParser.format(by) + ")";
    }
}
