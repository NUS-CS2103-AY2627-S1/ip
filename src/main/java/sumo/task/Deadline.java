package sumo.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * A task that should be completed by a specified date or time.
 */
public class Deadline extends Task {
    private final LocalDateTime by;
    private final boolean hasTime;

    /**
     * Creates a new incomplete deadline.
     *
     * @param description the task text.
     * @param by the date and time by which the task should be completed.
     */
    public Deadline(String description, LocalDateTime by) {
        this(description, by, true);
    }

    /**
     * Creates a new incomplete deadline for a date without a specified time.
     *
     * @param description the task text.
     * @param by the date by which the task should be completed.
     */
    public Deadline(String description, LocalDate by) {
        this(description, by.atStartOfDay(), false);
    }

    /**
     * Creates a deadline while retaining whether its input included a time.
     *
     * @param description the task text.
     * @param by the deadline date and time.
     * @param hasTime whether the user supplied a time.
     */
    public Deadline(String description, LocalDateTime by, boolean hasTime) {
        super(description);
        assert by != null : "A deadline must have a date or time.";
        assert hasTime || by.toLocalTime().equals(LocalTime.MIDNIGHT)
                : "A date-only deadline must be normalized to midnight.";
        this.by = by;
        this.hasTime = hasTime;
    }

    /**
     * Returns the deadline type icon.
     *
     * @return the deadline type icon.
     */
    @Override
    public String getTypeIcon() {
        return TaskType.DEADLINE.getStorageCode();
    }

    /**
     * Returns the date or time by which this task should be completed.
     *
     * @return the deadline date or time.
     */
    public LocalDateTime getBy() {
        return by;
    }

    /** Returns whether another deadline has the same description and due date. */
    @Override
    public boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other) && by.equals(((Deadline) other).by);
    }

    /**
     * Returns this deadline in the line-based format used for persistent storage.
     *
     * @return the task type, completion status, description, and deadline.
     */
    @Override
    public String toDataString() {
        return super.toDataString() + " | "
                + (hasTime ? by.toString() : by.toLocalDate().toString());
    }

    /**
     * Returns this deadline in the format used by Sumo's task list.
     *
     * @return the formatted deadline.
     */
    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] "
                + getDescription() + " (by: " + DateTimeDisplay.format(by, hasTime) + ")";
    }
}
