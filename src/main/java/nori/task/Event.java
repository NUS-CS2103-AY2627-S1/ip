package nori.task;

/**
 * Represents a task that takes place over a given period.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates an incomplete event task.
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

    @Override
    protected String getTypeIcon() {
        return "E";
    }

    /** {@inheritDoc} */
    @Override
    public String toStorageString() {
        return "E | " + (isDone() ? "1" : "0") + " | " + description + " | " + from + " | " + to;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
