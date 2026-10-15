package nori.task;

/**
 * Represents a task that occurs between a start and end time.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates an event.
     *
     * @param description description shown to the user.
     * @param from start text supplied by the user.
     * @param to end text supplied by the user.
     */
    public Event(String description, String from, String to) {
        super(description);
        assert from != null : "Event start must not be null";
        assert !from.isBlank() : "Event start must not be blank";
        assert to != null : "Event end must not be null";
        assert !to.isBlank() : "Event end must not be blank";
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event start text.
     *
     * @return start text supplied by the user
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the event end text.
     *
     * @return end text supplied by the user
     */
    public String getTo() {
        return to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
