package edith.task;

import java.time.LocalDateTime;

import edith.util.DateFormatter;

/** Represents a task with start and end dates, each optionally with a time. */
public class Event extends Task {
    /** The date and optional time at which this event starts. */
    private final LocalDateTime from;
    /** Whether the event start includes a time. */
    private final boolean hasStartTime;
    /** The date and optional time at which this event ends. */
    private final LocalDateTime to;
    /** Whether the event end includes a time. */
    private final boolean hasEndTime;

    /**
     * Creates an event with its description, start date-time, and end date-time.
     *
     * @param description the text describing the event
     * @param from the start date-time
     * @param hasStartTime whether the start includes a time
     * @param to the end date-time
     * @param hasEndTime whether the end includes a time
     */
    public Event(String description, LocalDateTime from, boolean hasStartTime, LocalDateTime to,
            boolean hasEndTime) {
        super(description);
        this.from = from;
        this.hasStartTime = hasStartTime;
        this.to = to;
        this.hasEndTime = hasEndTime;
    }

    /**
     * Returns this event in the format used by the task list.
     *
     * @return the event type, status icon, description, start date-time, and end date-time
     */
    @Override
    public String toString() {
        return toStorageString() + getTagSuffix();
    }

    @Override
    public String toStorageString() {
        return "[E]" + getStatusIcon() + " " + getDescription()
                + " (from: " + DateFormatter.formatForDisplay(from, hasStartTime)
                + " to: " + DateFormatter.formatForDisplay(to, hasEndTime) + ")";
    }

    /**
     * Returns the identifier for an event task.
     *
     * @return {@code event}
     */
    @Override
    protected String getTaskType() {
        return "event";
    }
}
