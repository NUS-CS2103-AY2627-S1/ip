package bambolino.task;

/**
 * A task that has a starting and ending date or time.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates an incomplete event task.
     *
     * @param description The event description.
     * @param from The starting date or time, stored as text.
     * @param to The ending date or time, stored as text.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns this event task in the list-display format.
     *
     * @return The task type, status icon, description, start, and end.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }

    /** Returns this event in a format used by persistent storage. */
    @Override
    public String toStorageString() {
        return "E|" + (isDone ? "1" : "0") + "|" + encode(description)
                + "|" + encode(from) + "|" + encode(to);
    }
}
