package jeremy.task;

/** A task that starts and ends at specific date/times. */
public class Event extends Task {
    protected String from;
    protected String to;

    public Event(String description, String from, String to) {
        super(description);
        assert from != null && !from.isBlank() : "Event must have a start time";
        assert to != null && !to.isBlank() : "Event must have an end time";
        this.from = from;
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    @Override
    protected String getTypeIcon() {
        return "E";
    }

    @Override
    public String toString() {
        return super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
