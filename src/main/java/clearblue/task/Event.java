package clearblue.task;

/**
 * Represents a task that takes place between a start and an end date or time.
 */
public class Event extends Task {
    private final TaskDateTime from;
    private final TaskDateTime to;

    /**
     * Creates an incomplete event.
     *
     * @param description description of the event
     * @param from date or time when the event starts; a {@code yyyy-MM-dd}
     *     value is understood as a real date, anything else is kept as free
     *     text
     * @param to date or time when the event ends; same parsing rule as
     *     {@code from}
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = new TaskDateTime(from);
        this.to = new TaskDateTime(to);
    }

    /**
     * Returns the date or time when this event starts.
     *
     * @return the event's {@code from} value
     */
    public TaskDateTime getFrom() {
        return from;
    }

    /**
     * Returns the date or time when this event ends.
     *
     * @return the event's {@code to} value
     */
    public TaskDateTime getTo() {
        return to;
    }

    /**
     * Returns this event's display text, e.g.
     * {@code "[E][X] trip (from: Jun 06 2019 to: Jun 08 2019)"}.
     *
     * @return display text for this event
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
