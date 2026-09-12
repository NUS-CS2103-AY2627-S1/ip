package minweeder.task;

import java.util.Objects;

/**
 * A task that spans a period of time, from a start point to an end point.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates an event task.
     *
     * @param description what the event is.
     * @param from when the event starts.
     * @param to when the event ends.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toFileString() {
        return "E | " + super.toFileFields() + " | " + this.from + " | " + this.to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + this.from + " to: " + this.to + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Event)) {
            return false;
        }
        Event otherEvent = (Event) other;
        return this.getDescription().equals(otherEvent.getDescription())
                && this.from.equals(otherEvent.from)
                && this.to.equals(otherEvent.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Event.class, this.getDescription(), this.from, this.to);
    }
}
