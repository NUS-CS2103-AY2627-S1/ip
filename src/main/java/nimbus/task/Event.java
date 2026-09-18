package nimbus.task;

/** Represents a task that occurs over a period. */
public class Event extends Task {
    protected final String from;
    protected final String to;

    /**
     * Creates an event task.
     *
     * @param description Description of the task.
     * @param from Start of the event.
     * @param to End of the event.
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

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
