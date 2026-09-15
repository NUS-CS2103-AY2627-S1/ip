package tem;

/**
 * Represents a task that happens between a start and end time.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates an incomplete event task.
     *
     * @param description description supplied by the user.
     * @param from event start time.
     * @param to event end time.
     */
    public Event(String description, String from, String to) {
        super(description, TaskType.EVENT);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the start time of this event.
     *
     * @return start time text
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the end time of this event.
     *
     * @return end time text
     */
    public String getTo() {
        return to;
    }

    /**
     * Returns the event period used when comparing this task with another task.
     *
     * @return start and end details separated by a non-display character
     */
    @Override
    protected String getScheduleDetails() {
        return from + "\u0000" + to;
    }

    /**
     * Returns this event in the on-disk format {@code E | done | description | from | to}.
     *
     * @return storage line for this event
     */
    @Override
    public String toStorageString() {
        return "E | " + (isDone() ? "1" : "0") + " | " + escapeStorageField(getDescription())
                + " | " + escapeStorageField(from) + " | " + escapeStorageField(to);
    }

    /**
     * Returns this event with its start and end times.
     *
     * @return task type, completion status, description, and event period
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
