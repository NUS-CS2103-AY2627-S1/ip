package alfred.task;

import java.time.LocalDateTime;

/**
 * Represents a task occurring between a specified start and end time.
 */
public class Event extends Task {
    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Creates an incomplete event with its description, start time, and end time.
     *
     * @param description text describing the event
     * @param from event start date and time
     * @param to event end date and time
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(TaskType.EVENT, description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event start time for storage purposes.
     *
     * @return the event start date and time
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns the event end time for storage purposes.
     *
     * @return the event end date and time
     */
    public LocalDateTime getTo() {
        return to;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasSameIdentityAs(Task other) {
        return other instanceof Event event && super.hasSameIdentityAs(other)
                && from.equals(event.from) && to.equals(event.to);
    }

    /**
     * Returns this event with its type, completion status, start time, and end time.
     *
     * @return the formatted event task
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + TaskDateTime.format(from) + " to: "
                + TaskDateTime.format(to) + ")";
    }
}
