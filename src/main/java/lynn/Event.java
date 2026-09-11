package lynn;

/** Represents a task with a start and end time description. */
public class Event extends Task {
    private final String from;

    private final String to;

    /**
     * Creates an event with the given description, start, and end.
     *
     * @param description the event description
     * @param from the event start text
     * @param to the event end text
     */
    public Event(String description, String from, String to) {
        super(description, TaskType.EVENT);
        this.from = from;
        this.to = to;
    }

    String getFrom() {
        return from;
    }

    String getTo() {
        return to;
    }

    /** Returns the event in Lynn's list-display format. */
    @Override
    public String toString() {
        return super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
