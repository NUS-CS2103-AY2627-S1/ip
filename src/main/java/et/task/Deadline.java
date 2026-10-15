package et.task;

import et.parser.DateTimeParser;

import java.time.LocalDateTime;

/**
 * Represents a task that must be completed by a given date or time.
 */
public class Deadline extends Task {
    /** The deadline's actual due date and time. */
    private final LocalDateTime by;

    /** Whether the user included a time in the deadline input. */
    private final boolean hasTime;

    /**
     * Creates a deadline task that is not done yet.
     *
     * @param description the text describing the task
     * @param by the deadline's due date and time
     * @param hasTime whether the deadline includes a time
     */
    public Deadline(String description, LocalDateTime by, boolean hasTime) {
        super(TaskType.DEADLINE, description);
        if (by == null) {
            throw new IllegalArgumentException("Deadline date or time cannot be null.");
        }
        this.by = by;
        this.hasTime = hasTime;
    }

    /**
     * Returns this deadline's due date or time.
     *
     * @return the deadline's date and time
     */
    public LocalDateTime getBy() {
        return by;
    }

    /**
     * Returns whether this deadline has a supplied time as well as a date.
     *
     * @return {@code true} when a time was supplied
     */
    public boolean hasTime() {
        return hasTime;
    }

    /**
     * Returns this deadline in the format used by the task list.
     *
     * @return the deadline type, status icon, description, and due date or time
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + DateTimeParser.format(by, hasTime) + ")";
    }
}
