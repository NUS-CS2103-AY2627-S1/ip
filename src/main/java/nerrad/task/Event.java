package nerrad.task;

/**
 * Represents a task that occurs between a start and an end date or time.
 */
public class Event extends Task {
    /** Date or time at which the event starts. */
    protected String from;

    /** Date or time at which the event ends. */
    protected String to;

    /**
     * Creates an incomplete event task.
     *
     * @param description Description of the event.
     * @param from Date or time at which the event starts.
     * @param to Date or time at which the event ends.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the start date or time as entered by the user.
     *
     * @return Event start date or time.
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the end date or time as entered by the user.
     *
     * @return Event end date or time.
     */
    public String getTo() {
        return to;
    }

    /**
     * Returns the event in its user-facing display format.
     *
     * @return Event type icon, common task display, and start/end details.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}

