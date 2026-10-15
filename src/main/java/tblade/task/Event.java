package tblade.task;

import java.util.Objects;

/**
 * Represents a task that starts and ends at specified times.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates an unfinished event task.
     *
     * @param description text that describes the event
     * @param from the event start text entered by the user
     * @param to the event end text entered by the user
     */
    public Event(String description, String from, String to) {
        super(description, TaskType.EVENT);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event start text for storage.
     *
     * @return the event start text
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the event end text for storage.
     *
     * @return the event end text
     */
    public String getTo() {
        return to;
    }

    /**
     * Returns the {@code (from: ... to: ...)} suffix shown after an event's description.
     *
     * @return the formatted start and end, wrapped as a "from ... to ..." suffix
     */
    @Override
    protected String getTimeDetails() {
        return " (from: " + from + " to: " + to + ")";
    }

    /**
     * Returns whether this event and the given object have the same type, description, start,
     * and end.
     *
     * @param other the object to compare against
     * @return {@code true} if {@code other} is an event with the same description, start, and end
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Event) || !super.equals(other)) {
            return false;
        }
        Event event = (Event) other;
        return from.equals(event.from) && to.equals(event.to);
    }

    /**
     * Returns a hash code consistent with {@link #equals(Object)}.
     *
     * @return the hash code for this event's type, description, start, and end
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), from, to);
    }
}
