package quackers;

/**
 * Represents a task that happens between a start and end time.
 */
public class Event extends Task {
    private final String startTime;
    private final String endTime;

    /**
     * Creates an incomplete event task.
     *
     * @param description the event description.
     * @param startTime the event start time, kept as text.
     * @param endTime the event end time, kept as text.
     */
    public Event(String description, String startTime, String endTime) {
        super(TaskType.EVENT, description);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    /**
     * Formats this event with its type, completion status, and time range.
     *
     * @return the formatted event
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + startTime + " to: " + endTime + ")";
    }
}
