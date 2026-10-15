package esther.task;

/**
 * Represents a task occurring between two times.
 */
public class Event extends Task {
    private final String startTime;
    private final String endTime;

    /**
     * Creates an event with the specified description and time range.
     *
     * @param description Description of the event.
     * @param startTime Starting date or time of the event.
     * @param endTime Ending date or time of the event.
     */
    public Event(String description, String startTime, String endTime) {
        super(description, TaskType.EVENT);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public String toDataString() {
        return super.toDataString() + " | "
                + escapeDataField(startTime) + " | " + escapeDataField(endTime);
    }

    @Override
    public String toString() {
        return String.format(
                "%s (from: %s to: %s)",
                super.toString(),
                startTime,
                endTime
        );
    }
}
