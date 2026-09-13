package chimpanzinibananini.task;

/**
 * Represents a task that occurs over a specified period.
 */
public class Event extends Task {
    private String from;
    private String to;

    /**
     * Creates an incomplete event with the specified description, start, and end values.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String getTypeIcon() {
        return "E";
    }

    @Override
    public String toDataString() {
        return super.toDataString() + " | " + encodeField(from) + " | " + encodeField(to);
    }

    @Override
    public String toString() {
        return super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
