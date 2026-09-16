package downtowngurl.task;

import java.time.LocalDateTime;

/**
 * Represents a task that should be completed by a specific date or time.
 */
public class Deadline extends Task {
    private final LocalDateTime by;

    /**
     * Creates a deadline task.
     *
     * @param description Details of the task.
     * @param by Date and time by which the task should be completed.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description, TaskType.DEADLINE);
        this.by = by;
    }

    /**
     * Returns a storage-safe line containing this deadline's description and due time.
     *
     * @return Data file representation of this deadline.
     */
    @Override
    public String toStorageString() {
        return super.toStorageString() + " * " + escapeStorageField(TaskDateTime.formatForStorage(this.by))
                + getRecurrenceStorageText();
    }

    /**
     * Returns the due time used for sorting deadlines by date.
     *
     * @return Due date and time.
     */
    @Override
    public LocalDateTime getSortDateTime() {
        return this.by;
    }

    /**
     * Returns the user-facing text representation of this deadline.
     *
     * @return Task text with formatted due time.
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + TaskDateTime.formatForDisplay(this.by) + ")"
                + getRecurrenceDisplayText();
    }
}
