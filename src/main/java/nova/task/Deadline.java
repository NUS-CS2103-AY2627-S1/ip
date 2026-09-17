package nova.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Represents a task that must be completed by a particular date or time.
 */
public class Deadline extends Task {
    private final LocalDateTime dueDateTime;
    private final boolean hasTime;

    /**
     * Creates an incomplete deadline with the given description and due time.
     *
     * @param description description of the deadline.
     * @param dueDateTime date and optional time by which the task must be completed.
     * @param hasTime whether the due value includes an explicit time.
     */
    public Deadline(String description, LocalDateTime dueDateTime, boolean hasTime) {
        super(description);
        assert dueDateTime != null : "Deadline due date/time must not be null";
        assert hasTime || dueDateTime.toLocalTime().equals(LocalTime.MIDNIGHT)
                : "A date-only deadline must use midnight";
        this.dueDateTime = dueDateTime;
        this.hasTime = hasTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getTypeIcon() {
        return "D";
    }

    /**
     * Returns this deadline's due date and optional time.
     *
     * @return due date/time.
     */
    public LocalDateTime getDueDateTime() {
        return dueDateTime;
    }

    /**
     * Returns whether the deadline includes an explicit time.
     *
     * @return {@code true} if the due value includes a time.
     */
    public boolean hasTime() {
        return hasTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return dueDateTime.toLocalDate().equals(date);
    }

    /**
     * Formats this deadline for storage, including its due date and optional time.
     *
     * @return task fields followed by the stored due date and optional time.
     */
    @Override
    public String toFileString() {
        return super.toFileString() + " | "
                + TaskDateTime.formatForStorage(dueDateTime, hasTime);
    }

    /**
     * Formats this deadline for display, including its due date and optional time.
     *
     * @return display form of this deadline.
     */
    @Override
    public String toString() {
        return super.toString() + " (by: "
                + TaskDateTime.formatForDisplay(dueDateTime, hasTime) + ")";
    }
}
