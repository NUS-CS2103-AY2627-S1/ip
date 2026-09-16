package mira.task;

/**
 * A task that occurs between stated start and end times.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates an incomplete event.
     *
     * @param description human-readable task description.
     * @param from start date or time as entered by the user.
     * @param to end date or time as entered by the user.
     */
    public Event(String description, String from, String to) {
        super(description, TaskType.EVENT);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event's start text exactly as entered by the user.
     *
     * @return the start date or time text.
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the event's end text exactly as entered by the user.
     *
     * @return the end date or time text.
     */
    public String getTo() {
        return to;
    }

    /** {@inheritDoc} */
    @Override
    protected String getDetails() {
        return " (from: " + from + " to: " + to + ")";
    }
}
