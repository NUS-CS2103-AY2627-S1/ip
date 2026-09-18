package eden.task;

/**
 * Represents a task that takes place between two times.
 */
public class Event extends Task {
    private String from;
    private String to;

    /**
     * Creates an unmarked event task.
     *
     * @param description task description.
     * @param from text describing when the event starts.
     * @param to text describing when the event ends.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toDataString() {
        return "E | " + (isMarked() ? "1" : "0") + " | "
                + escapeDataField(getDescription()) + " | " + escapeDataField(from)
                + " | " + escapeDataField(to);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
