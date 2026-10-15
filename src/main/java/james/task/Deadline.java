package james.task;

import java.time.LocalDate;

/**
 * Represents a task that needs to be completed by a specific deadline.
 */
public class Deadline extends Task {
    private static final String DEADLINE_STORAGE_PREFIX = "D | ";
    private static final String STORAGE_FIELD_SEPARATOR = " | ";
    private static final String DEADLINE_DISPLAY_PREFIX = "[D]";
    private static final String DEADLINE_DATE_FORMAT = " (by: %s)";

    private final LocalDate by;

    /**
     * Initializes an uncompleted deadline task with the given description and due date.
     *
     * @param description Description of the deadline task.
     * @param by The deadline date.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    @Override
    public Task copy() {
        Task copy = new Deadline(description, by);
        copy.isDone = isDone;
        return copy;
    }

    /**
     * Returns the deadline date.
     *
     * @return The deadline date.
     */
    public LocalDate getBy() {
        return by;
    }

    /**
     * Returns the string representation of the deadline task formatted for file storage.
     *
     * @return Formatted deadline string for storage.
     */
    @Override
    public String toFileString() {
        return DEADLINE_STORAGE_PREFIX + super.toFileString() + STORAGE_FIELD_SEPARATOR + by;
    }

    /**
     * Returns the string representation of the deadline task for display.
     *
     * @return Formatted display string of the deadline task.
     */
    @Override
    public String toString() {
        return DEADLINE_DISPLAY_PREFIX + super.toString() + DEADLINE_DATE_FORMAT.formatted(by.format(DISPLAY_FORMAT));
    }
}
