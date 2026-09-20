package goop.task;

/**
 * Represents a task that takes place between a start and end date or time.
 */
public class Event extends Task {
    /** Text describing when the event starts. */
    private final String from;

    /** Text describing when the event ends. */
    private final String to;

    /**
     * Creates an incomplete event task.
     *
     * @param description Text describing the task.
     * @param from Date or time at which the event starts.
     * @param to Date or time at which the event ends.
     * @throws IllegalArgumentException If the schedule is invalid or does not end after it starts.
     */
    public Event(String description, String from, String to) {
        super(description);
        EventSchedule.validate(from, to);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event's starting date or time.
     *
     * @return Event start text.
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the event's ending date or time.
     *
     * @return Event end text.
     */
    public String getTo() {
        return to;
    }

    /**
     * Returns this task with its event type icon and start and end text.
     *
     * @return The formatted event task.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
