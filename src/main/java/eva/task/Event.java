package eva.task;

/**
 * Represents a task that takes place over a time period.
 */
public class Event extends Task {
    protected String from;
    protected String to;

    /**
     * Creates an event with the specified description and time period.
     *
     * @param description Description of the event.
     * @param from Start of the event.
     * @param to End of the event.
     */
    public Event(String description, String from, String to) {
        super(description, TaskType.EVENT);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toFileString() {
        return "E | " + getFileStatus() + " | " + description
                + " | " + from + " | " + to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + from + " to: " + to + ")";
    }
}
