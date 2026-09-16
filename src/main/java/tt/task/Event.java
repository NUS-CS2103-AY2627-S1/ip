package tt.task;

/**
 * Represents a task associated with starting and ending times.
 */
public class Event extends Task {
    private final String startTime;
    private final String endTime;

    /**
     * Creates an incomplete event with its time range.
     *
     * @param description description of the event.
     * @param startTime description of the event's starting time.
     * @param endTime description of the event's ending time.
     */
    public Event(String description, String startTime, String endTime) {
        super(description);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Returns the event's starting time description.
     *
     * @return event's starting time description.
     */
    public String getFrom() {
        return startTime;
    }

    /**
     * Returns the event's ending time description.
     *
     * @return event's ending time description.
     */
    public String getTo() {
        return endTime;
    }

    /**
     * Returns the event with its type marker, status, and time range.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + startTime + " to " + endTime + ")";
    }
}
