package bobby.task;

import java.time.LocalDateTime;

import bobby.util.DateTimeParser;

/**
 * Represents a task that happens from one date or time to another.
 */
public class Event extends Task {
    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Creates an event task.
     *
     * @param description text that describes the event.
     * @param from start date/time.
     * @param to end date/time.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description, TaskType.EVENT);
        assert from != null : "Event start date should be non-null.";
        assert to != null : "Event end date should be non-null.";

        this.from = from;
        this.to = to;
    }

    /**
     * Returns this event in the chatbot's list display format.
     *
     * @return event type, status, description, start, and end.
     */
    @Override
    public String toString() {
        return baseDisplayString() + " (from: " + DateTimeParser.formatForDisplay(from)
                + " to: " + DateTimeParser.formatForDisplay(to) + ")" + tagsDisplayString();
    }

    /**
     * Returns a line that can be saved in the data file.
     *
     * @return serialized event data.
     */
    @Override
    public String toStorageString() {
        return typeAndStatusStorageString() + " | " + getDescription()
                + " | " + from + " | " + to + tagsStorageString();
    }

    /**
     * Returns a stable key used to detect duplicate events.
     *
     * @return event type, description, start date/time, and end date/time.
     */
    @Override
    public String getIdentityKey() {
        return super.getIdentityKey() + " | " + from + " | " + to;
    }
}
