package pavanmaxxer;

/**
 * Represents a task occurring between a start and end time.
 */
public class Event extends Task {
    /** Start-time text supplied by the user. */
    protected String from;
    /** End-time text supplied by the user. */
    protected String to;

    /**
     * Creates an incomplete event task.
     *
     * @param description Description of the task.
     * @param from Start-time text supplied by the user.
     * @param to End-time text supplied by the user.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event start text.
     *
     * @return Event start text.
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the event end text.
     *
     * @return Event end text.
     */
    public String getTo() {
        return to;
    }

    @Override
    public boolean hasSameDetails(Task other) {
        if (!(other instanceof Event event)) {
            return false;
        }
        return super.hasSameDetails(other)
                && from.equalsIgnoreCase(event.from)
                && to.equalsIgnoreCase(event.to);
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
