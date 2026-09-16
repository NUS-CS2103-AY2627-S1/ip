package walter.task;

/**
 * Represents a task that occurs at one specified time or across a start/end period.
 */
public class Event extends Task {
    private final String atTime;
    private final String startTime;
    private final String endTime;

    /**
     * Creates an event task with the given description and time text.
     *
     * @param description Description of the event task.
     * @param atTime Text describing when the event occurs.
     */
    public Event(String description, String atTime) {
        super(description);
        assert atTime != null && !atTime.isBlank()
                : "Event time should be validated before task creation";
        this.atTime = atTime;
        startTime = null;
        endTime = null;
    }

    /**
     * Creates an event task that starts and ends at the given times.
     *
     * @param description Description of the event task.
     * @param startTime Text describing when the event starts.
     * @param endTime Text describing when the event ends.
     */
    public Event(String description, String startTime, String endTime) {
        super(description);
        assert startTime != null && !startTime.isBlank()
                && endTime != null && !endTime.isBlank()
                : "Event time range should be validated before task creation";
        atTime = null;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Reports whether this event uses the {@code /at} representation.
     *
     * @return {@code true} for an at-time event, or {@code false} for a time range.
     */
    public boolean isAtFormat() {
        return atTime != null;
    }

    /**
     * Returns the at-time text for persistence.
     *
     * @return At-time text, or {@code null} for a time-range event.
     */
    public String getAt() {
        return atTime;
    }

    /**
     * Returns the start text for persistence.
     *
     * @return Start text, or {@code null} for an at-time event.
     */
    public String getFrom() {
        return startTime;
    }

    /**
     * Returns the end text for persistence.
     *
     * @return End text, or {@code null} for an at-time event.
     */
    public String getTo() {
        return endTime;
    }

    /**
     * Returns the Event type marker, task status, description, and configured time details.
     *
     * @return Display form using either {@code at:} or {@code from: ... to: ...}.
     */
    @Override
    public String toString() {
        String timeDetails = isAtFormat()
                ? "at: " + atTime
                : "from: " + startTime + " to: " + endTime;
        return "[E]" + super.toString() + " (" + timeDetails + ")";
    }
}
