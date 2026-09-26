package dulio.task;

/**
 * A task that occurs between a specified start and end date or time.
 */
public class Event extends Task {
    private String from;
    private String to;

    /**
     * Creates an event task.
     *
     * @param description The event description.
     * @param from The date or time when the event starts.
     * @param to The date or time when the event ends.
     */
    public Event(String description, String from, String to) {
        super(description);
        assert from != null && !from.isBlank() : "Event start must not be blank";
        assert to != null && !to.isBlank() : "Event end must not be blank";
        this.from = from;
        this.to = to;
    }

    @Override
    public String getTypeIcon() {
        return "E";
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    /**
     * Returns the event description together with its start and end times.
     *
     * @return The formatted event.
     */
    @Override
    public String toString() {
        return description + " (from: " + from + " to: " + to + ")";
    }
}
