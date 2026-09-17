package yuki.task;

import java.util.Objects;

import yuki.time.DateTimeParser;
import yuki.time.TaskDateTime;

/**
 * Represents an event with a specific start and end date-time.
 */
public class Event extends Task {
    /** The date and time at which this event starts. */
    private final TaskDateTime from;
    /** The date and time at which this event ends. */
    private final TaskDateTime to;

    /**
     * Creates an event task with a start and end time.
     *
     * @param description The text describing the event.
     * @param from The event's start date or time.
     * @param to The event's end date or time.
     */
    public Event(String description, TaskDateTime from, TaskDateTime to) {
        super(description);
        this.from = Objects.requireNonNull(from, "event start must not be null");
        this.to = Objects.requireNonNull(to, "event end must not be null");
        if (from.hasComparableType(to) && !from.isBefore(to)) {
            throw new IllegalArgumentException("An event must end after it starts");
        }
    }

    /**
     * Returns the date and time at which this event starts.
     *
     * @return This event's start date and time.
     */
    public TaskDateTime getFrom() {
        return from;
    }

    /**
     * Returns the date and time at which this event ends.
     *
     * @return This event's end date and time.
     */
    public TaskDateTime getTo() {
        return to;
    }

    /** Returns whether another event has the same description, start, and end. */
    @Override
    public boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other)
                && from.hasSameValueAs(((Event) other).from)
                && to.hasSameValueAs(((Event) other).to);
    }

    /**
     * Returns this event task in the format used in the task list.
     *
     * @return The formatted event task.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + DateTimeParser.format(from)
                + " to: " + DateTimeParser.format(to) + ")";
    }
}
