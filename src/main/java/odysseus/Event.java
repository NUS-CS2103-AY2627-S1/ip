package odysseus;

/**
 * A task with start and end date or time strings.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates an incomplete event task.
     *
     * @param description text describing the event.
     * @param from start date or time text, retained without parsing.
     * @param to end date or time text, retained without parsing.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    protected String getTypeMarker() {
        return "E";
    }

    @Override
    protected String getDetails() {
        return " (from: " + from + " to: " + to + ")";
    }

    /**
     * Returns the saved event start text.
     */
    protected String getFrom() {
        return from;
    }

    /**
     * Returns the saved event end text.
     */
    protected String getTo() {
        return to;
    }
}
