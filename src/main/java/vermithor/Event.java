package vermithor;

/**
 * Represents a task that occurs over a specified time range.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates an incomplete event task.
     *
     * @param description the event description
     * @param from when the event starts
     * @param to when the event ends
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /** Returns the event start text. */
    public String getFrom() {
        return from;
    }

    /** Returns the event end text. */
    public String getTo() {
        return to;
    }

    /**
     * Returns the event format used in the task list.
     *
     * @return the task type, status, description, start time, and end time
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
