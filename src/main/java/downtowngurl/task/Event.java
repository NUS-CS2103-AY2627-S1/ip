package downtowngurl.task;

import java.time.LocalDateTime;

/**
 * Represents a task that happens between a start date or time and an end date or time.
 */
public class Event extends Task {
    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Creates an event task.
     *
     * @param description Details of the task.
     * @param from Date and time when the event starts.
     * @param to Date and time when the event ends.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description, TaskType.EVENT);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns a storage-safe line containing this event's description and time range.
     *
     * @return Data file representation of this event.
     */
    @Override
    public String toStorageString() {
        return super.toStorageString() + " * " + escapeStorageField(TaskDateTime.formatForStorage(this.from))
                + " * " + escapeStorageField(TaskDateTime.formatForStorage(this.to)) + getRecurrenceStorageText();
    }

    /**
     * Returns the start time used for sorting events by date.
     *
     * @return Start date and time.
     */
    @Override
    public LocalDateTime getSortDateTime() {
        return this.from;
    }

    /**
     * Returns the user-facing text representation of this event.
     *
     * @return Task text with formatted start and end times.
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + TaskDateTime.formatForDisplay(this.from)
                + " to: " + TaskDateTime.formatForDisplay(this.to) + ")" + getRecurrenceDisplayText();
    }
}
