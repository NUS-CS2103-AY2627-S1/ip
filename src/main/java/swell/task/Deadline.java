package swell.task;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Represents a task that should be completed by a deadline.
 */
public class Deadline extends Task {
    private final TaskDateTime by;

    /**
     * Creates a deadline task with the given description and deadline date.
     *
     * @param description task description.
     * @param by deadline date.
     */
    public Deadline(String description, LocalDate by) {
        this(description, TaskDateTime.of(by));
    }

    /**
     * Creates a deadline task with the given description and deadline date-time.
     *
     * @param description task description.
     * @param by deadline date-time.
     */
    public Deadline(String description, TaskDateTime by) {
        super(TaskType.DEADLINE, description);
        this.by = by;
    }

    /**
     * Creates a deadline task with the given description, deadline date, and tags.
     *
     * @param description task description.
     * @param by deadline date.
     * @param tags task tags.
     */
    public Deadline(String description, LocalDate by, ArrayList<String> tags) {
        this(description, TaskDateTime.of(by), tags);
    }

    /**
     * Creates a deadline task with the given description, deadline date-time, and tags.
     *
     * @param description task description.
     * @param by deadline date-time.
     * @param tags task tags.
     */
    public Deadline(String description, TaskDateTime by, ArrayList<String> tags) {
        super(TaskType.DEADLINE, description, tags);
        this.by = by;
    }

    /**
     * Returns the deadline date.
     *
     * @return deadline date-time.
     */
    public TaskDateTime getBy() {
        return by;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + by.toDisplayString() + ")";
    }
}
