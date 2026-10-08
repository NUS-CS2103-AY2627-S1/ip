package chim.task;

/**
 * Represents a task that spans a specific start and end date/time.
 */
public class Event extends Task {

    protected String from;
    protected String to;

    /**
     * Creates an Event with the given description, start time, and end time.
     *
     * @param description Description of the event.
     * @param from Start date/time of the event.
     * @param to End date/time of the event.
     */
    public Event(String description, String from, String to) {
        super(description, TaskType.EVENT);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toFileFormat() {
        return super.toFileFormat() + " | " + from + " | " + to;
    }

    @Override
    protected String getDetailsKey() {
        return super.getDetailsKey() + "|" + from + "|" + to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
