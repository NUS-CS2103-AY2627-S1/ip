/**
 * Represents a task with start and end time strings.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates an event with a description, starting time, and ending time.
     *
     * @param description text describing the event
     * @param from the event start time, stored exactly as entered
     * @param to the event end time, stored exactly as entered
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    protected String getTypeCode() {
        return "E";
    }

    @Override
    protected String getDetails() {
        return " (from: " + from + " to: " + to + ")";
    }
}
