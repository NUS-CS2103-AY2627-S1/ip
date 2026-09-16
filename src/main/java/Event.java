/**
 * Represents a task with a start and end time.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates an incomplete event task.
     *
     * @param description text describing the event
     * @param from start time text supplied by the user
     * @param to end time text supplied by the user
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event start text supplied for this task.
     *
     * @return start text
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the event end text supplied for this task.
     *
     * @return end text
     */
    public String getTo() {
        return to;
    }

    /**
     * Returns the formatted event task.
     *
     * @return the task with its type, completion status, and time range
     */
    @Override
    public String toString() {
        return "[E][" + getStatusIcon() + "] " + getDescription()
                + " (from: " + from + " to: " + to + ")";
    }
}
