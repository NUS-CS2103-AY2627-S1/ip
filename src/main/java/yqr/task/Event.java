package yqr.task;

import java.util.Objects;

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
     * @param from start date or time as entered by the user.
     * @param to end date or time as entered by the user.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = Objects.requireNonNull(from, "Event start cannot be null");
        this.to = Objects.requireNonNull(to, "Event end cannot be null");
    }

    /**
     * Returns the event's starting details.
     *
     * @return start date or time as entered by the user.
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the event's ending details.
     *
     * @return end date or time as entered by the user.
     */
    public String getTo() {
        return to;
    }

    /**
     * Returns whether another event has the same description and time details.
     *
     * @param other task to compare.
     * @return {@code true} if both events have the same identifying details.
     */
    @Override
    public boolean hasSameDetails(Task other) {
        if (!super.hasSameDetails(other)) {
            return false;
        }
        Event otherEvent = (Event) other;
        return from.equals(otherEvent.from) && to.equals(otherEvent.to);
    }

    /**
     * Returns a displayable version of this event task.
     *
     * @return event type icon, task details, and start and end times.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
