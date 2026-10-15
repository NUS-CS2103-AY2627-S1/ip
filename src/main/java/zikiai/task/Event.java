package zikiai.task;

/**
 * Represents a task that takes place over a period of time.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates an incomplete event task.
     *
     * @param description description of the event.
     * @param from start time of the event.
     * @param to end time of the event.
     */
    public Event(String description, String from, String to) {
        super(description);
        assert from != null && !from.isBlank() : "Event start time must not be blank";
        assert to != null && !to.isBlank() : "Event end time must not be blank";
        this.from = from;
        this.to = to;
    }

    /**
     * Returns this event with its type, completion status, and time range.
     *
     * @return formatted event for display
     */
    @Override
    public String getDescription() {
        return "[E]" + super.getDescription()
                + " (from: " + from + " to: " + to + ")";
    }

    /**
     * Returns this event in the format used by the data file.
     *
     * @return storage representation of this event
     */
    @Override
    public String toDataString() {
        return "[E][" + getStatusIcon() + "]" + getPriorityStorageTag()
                + " | " + getDescriptionText()
                + " | " + from + " | " + to;
    }
}
