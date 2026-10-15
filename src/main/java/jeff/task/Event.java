package jeff.task;

/**
 * Represents an event with a starting and ending time.
 */
public class Event extends Task {
    private final String startTime;
    private final String endTime;

    /**
     * Creates an event with the specified description and time range.
     *
     * @param description Description of the event.
     * @param startTime Starting time of the event.
     * @param endTime Ending time of the event.
     */
    public Event(String description, String startTime, String endTime) {
        super(description);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + startTime + " to: " + endTime + ")";
    }

    /**
     * Returns the event's starting time.
     *
     * @return Starting time of the event.
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * Returns the event's ending time.
     *
     * @return Ending time of the event.
     */
    public String getEndTime() {
        return endTime;
    }
}
