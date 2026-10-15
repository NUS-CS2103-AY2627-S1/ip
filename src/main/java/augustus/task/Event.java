package augustus.task;

/**
 * Represents a task that occurs between a start time and an end time.
 */
public class Event extends Task {

    private final String startTime;
    private final String endTime;

    /**
     * Creates an event with the given description, start time, and end time.
     *
     * @param description Description of the event.
     * @param startTime Start time of the event.
     * @param endTime End time of the event.
     */
    public Event(String description, String startTime, String endTime) {
        super(description);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Returns the display representation of this event.
     *
     * @return String containing the event description, start time, and end time.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (From: " + startTime + " to: " + endTime + ")";
    }

    /**
     * Returns the representation of this event used for storage.
     *
     * @return String representation of the event for saving to a file.
     */
    @Override
    public String toFileString() {
        return String.format("E | %s | %s | %s | %s%s",
                isDone() ? "1" : "0",
                getDescription(),
                startTime,
                endTime,
                getTagForStorage());
    }
}
