package sine.task;

/**
 * Represents a task that takes place between a start and end time.
 */
public class Event extends Task {
    protected String from;
    protected String to;

    /**
     * Creates an incomplete event task.
     *
     * @param description Description of the event.
     * @param from Start date or time supplied by the user.
     * @param to End date or time supplied by the user.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event's start time.
     *
     * @return Start time.
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the event's end time.
     *
     * @return End time.
     */
    public String getTo() {
        return to;
    }

    /**
     * Formats this task with its type icon, start, and end.
     *
     * @return The formatted event task.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to " + to + ")";
    }
}
