package turtley.model;

import java.time.temporal.Temporal;
import java.util.Collection;

import turtley.exception.TurtleyException;
import turtley.util.DateTimeParser;

/**
 * A task that must be completed by a specified time.
 */
public class Deadline extends Task {

    private final Temporal by;

    /**
     * Creates an unfinished deadline.
     *
     * @param description the task description.
     * @param by the parsed deadline date or date-time.
     * @throws TurtleyException if the description is blank or {@code by} is null.
     */
    public Deadline(String description, Temporal by) {
        super(TaskType.DEADLINE, description);
        if (by == null) {
            throw new TurtleyException("Deadline date/time cannot be null.");
        }
        this.by = by;
    }

    /**
     * Creates an unfinished deadline with tags.
     *
     * @param description the task description.
     * @param by the parsed deadline date or date-time.
     * @param tags the task tags.
     * @throws TurtleyException if the description is blank, {@code by} is null, or the tags are invalid.
     */
    public Deadline(String description, Temporal by, Collection<String> tags) {
        super(TaskType.DEADLINE, description, tags);
        if (by == null) {
            throw new TurtleyException("Deadline date/time cannot be null.");
        }
        this.by = by;
    }

    /**
     * Creates an unfinished deadline from user-entered date text.
     *
     * @param description the task description.
     * @param by the deadline text.
     */
    public Deadline(String description, String by) {
        this(description, DateTimeParser.parse(by));
    }

    /**
     * Creates an unfinished deadline from user-entered date text with tags.
     *
     * @param description the task description.
     * @param by the deadline text.
     * @param tags the task tags.
     */
    public Deadline(String description, String by, Collection<String> tags) {
        this(description, DateTimeParser.parse(by), tags);
    }

    /**
     * Returns the parsed deadline value.
     *
     * @return the deadline as a {@link java.time.LocalDate} or {@link java.time.LocalDateTime}
     */
    public Temporal getBy() {
        return by;
    }

    /**
     * Returns the display form including the deadline.
     *
     * @return the formatted deadline.
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + DateTimeParser.format(by) + ")";
    }
}
