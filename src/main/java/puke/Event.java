package puke;

/** Represents a task scheduled between a start and end time. */
public class Event extends Task {
    protected String from;
    protected String to;

    /**
     * Creates an event with start and end descriptions.
     *
     * @param desc Event description.
     * @param from Event start.
     * @param to Event end.
     */
    public Event(String desc, String from, String to) {
        super(desc);
        this.from = from;
        this.to = to;
        this.type = TaskType.EVENT;
    }

    /**
     * Creates an event with the given completion state.
     *
     * @param desc Event description.
     * @param from Event start.
     * @param to Event end.
     * @param done Whether the event is complete.
     */
    public Event(String desc, String from, String to, boolean done) {
        this(desc, from, to);
        this.isDone = done;
    }

    @Override
    public String toString() {
        return String.format("[E][%s] %s (from: %s to: %s)", getStatusIcon(), desc, from, to);
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    @Override
    public boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other)
                && other instanceof Event otherEvent
                && from.equals(otherEvent.from)
                && to.equals(otherEvent.to);
    }
}
