package gunna.task;

/**
 * Represents an event task that occurs during a specific time period.
 * An event has a description, start time, end time, and completion status.
 */
public class Event extends Task {
    public static final String TASK_TYPE = "E";
    protected String from;
    protected String to;

    /**
     * Creates a new Event task with the given description and time period.
     *
     * @param description The description of the event.
     * @param from The start time of the event.
     * @param to The end time of the event.
     */
    public Event(String description, String from, String to) {
        super(description);
        assert from != null : "Event start time cannot be null";
        assert to != null : "Event end time cannot be null";
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the start time of the event.
     *
     * @return The start time string.
     */
    public String getFrom() {
        return this.from;
    }

    /**
     * Returns the end time of the event.
     *
     * @return The end time string.
     */
    public String getTo() {
        return this.to;
    }

    /**
     * Converts the event task to a file-safe format for storage.
     * Format: E | status | description | from | to
     *
     * @return A pipe-separated string representation of the event task.
     */
    @Override
    public String toFileFormat() {
        return TASK_TYPE + FIELD_SEPARATOR + (isDone ? STATUS_DONE : STATUS_NOT_DONE)
                + FIELD_SEPARATOR + description + FIELD_SEPARATOR + from + FIELD_SEPARATOR + to;
    }

    /**
     * Returns a string representation of the event task for display.
     * Format: [E][status icon] description (from: start to: end)
     *
     * @return The string representation of the event task.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
