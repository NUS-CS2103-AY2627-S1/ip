package avon.task;

import java.time.LocalDateTime;
import java.util.Objects;

import avon.util.DateTimeParser;

/**
 * Represents a task that takes place during a specified time interval.
 */
public class Event extends Task {
    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Creates an unfinished event task.
     *
     * @param description the text describing the event.
     * @param from the event start date and time.
     * @param to the event end date and time.
     * @throws NullPointerException if either date-time is null.
     * @throws IllegalArgumentException if the event does not end after it starts.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = Objects.requireNonNull(from, "The event start cannot be null.");
        this.to = Objects.requireNonNull(to, "The event end cannot be null.");
        if (!this.to.isAfter(this.from)) {
            throw new IllegalArgumentException("An event must end after it starts.");
        }
    }

    /**
     * Returns the event start date and time.
     *
     * @return the event start.
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns the event end date and time.
     *
     * @return the event end.
     */
    public LocalDateTime getTo() {
        return to;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasSameDetailsAs(Task other) {
        return super.hasSameDetailsAs(other)
                && from.equals(((Event) other).from)
                && to.equals(((Event) other).to);
    }

    /**
     * Returns this event with its type, completion status, and time interval.
     *
     * @return the formatted event task.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + DateTimeParser.format(from)
                + " to: " + DateTimeParser.format(to) + ")";
    }
}
