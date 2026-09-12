package chin.task;

/**
 * A task with a start and end time, rendered as {@code [E][ ] desc (from: X to: Y)}.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /** Creates a new event with the given description, start and end strings. */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }

    @Override
    public String serialize() {
        return "E | " + super.serialize() + " | " + from + " | " + to;
    }
}
