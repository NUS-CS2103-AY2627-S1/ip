package beemo.task;

/**
 * Represents a task that occurs between a start and end date or time.
 */
public class Event extends Task {
    private final String startTime;
    private final String endTime;

    /**
     * Creates an incomplete event with the specified description and times.
     *
     * @param description Description of the event.
     * @param startTime Start time of the event.
     * @param endTime End time of the event.
     */
    public Event(String description, String startTime, String endTime) {
        super(description);
        assert startTime != null && !startTime.isBlank() : "Event start time must not be blank";
        assert endTime != null && !endTime.isBlank() : "Event end time must not be blank";
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Returns the event's start time.
     *
     * @return Event start time.
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * Returns the event's end time.
     *
     * @return Event end time.
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * Returns the event's type, status, description, and times for display.
     *
     * @return Display form of the event.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + startTime + " to: " + endTime + ")";
    }
}
