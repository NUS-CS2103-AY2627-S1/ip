package fox.task;

import java.util.Objects;

/**
 * Represents a task that takes place at an event.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates a new unfinished event task.
     *
     * @param description the task description
     * @param from the event start time
     * @param to the event end time
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event start time for persistence.
     *
     * @return the event start time
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the event end time for persistence.
     *
     * @return the event end time
     */
    public String getTo() {
        return to;
    }

    @Override
    public boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other)
                && Objects.equals(from, ((Event) other).from)
                && Objects.equals(to, ((Event) other).to);
    }

    /**
     * Returns this event task in its display format.
     *
     * @return the formatted event task
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
