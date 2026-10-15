package lebron;

/**
 * Represents an event task that occurs between a start and end time.
 */
public class Event extends Task {

    protected String from;
    protected String to;

    /**
     * Creates a new event task with the given description,
     * start time, and end time.
     *
     * @param description description of the event
     * @param from start time of the event
     * @param to end time of the event
     */
    public Event(String description, String from, String to) {
        super(description);
        this.type = TaskType.EVENT;
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the string representation of the event task.
     *
     * @return formatted event task including its start and end times
     */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + from + " to: " + to + ")";
    }
}
