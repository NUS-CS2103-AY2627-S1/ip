package toothless.task;

/**
 * Represents a task that takes place between specified start and end times.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates an incomplete event task.
     *
     * @param description description of the event.
     * @param from event start date or time.
     * @param to event end date or time.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event's starting date or time.
     *
     * @return event start date or time
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the event's ending date or time.
     *
     * @return event end date or time
     */
    public String getTo() {
        return to;
    }

    /**
     * Returns this event in the chatbot's display format.
     *
     * @return formatted event
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
