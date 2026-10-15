package et.task;

import et.parser.DateTimeParser;

import java.time.LocalDateTime;

/**
 * Represents a task with a start and end date or time.
 */
public class Event extends Task {
    /** The event's actual start date and time. */
    private final LocalDateTime from;

    /** The event's actual end date and time. */
    private final LocalDateTime to;

    /** Whether the event start input includes a time. */
    private final boolean hasStartTime;

    /** Whether the event end input includes a time. */
    private final boolean hasEndTime;

    /**
     * Creates an event task that is not done yet.
     *
     * @param description the text describing the task
     * @param from the event start date and time
     * @param hasStartTime whether the event start includes a time
     * @param to the event end date and time
     * @param hasEndTime whether the event end includes a time
     */
    public Event(String description, LocalDateTime from, boolean hasStartTime,
                 LocalDateTime to, boolean hasEndTime) {
        super(TaskType.EVENT, description);
        if (from == null) {
            throw new IllegalArgumentException("Event start date or time cannot be null.");
        }
        if (to == null) {
            throw new IllegalArgumentException("Event end date or time cannot be null.");
        }
        this.from = from;
        this.to = to;
        this.hasStartTime = hasStartTime;
        this.hasEndTime = hasEndTime;
    }

    /**
     * Returns this event's start date or time.
     *
     * @return the event's start date and time
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns this event's end date or time.
     *
     * @return the event's end date and time
     */
    public LocalDateTime getTo() {
        return to;
    }

    /**
     * Returns whether the event start input includes a time.
     *
     * @return {@code true} when a start time was supplied
     */
    public boolean hasStartTime() {
        return hasStartTime;
    }

    /**
     * Returns whether the event end input includes a time.
     *
     * @return {@code true} when an end time was supplied
     */
    public boolean hasEndTime() {
        return hasEndTime;
    }

    /**
     * Returns this event in the format used by the task list.
     *
     * @return the event type, status icon, description, and time range
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + DateTimeParser.format(from, hasStartTime)
                + " to: " + DateTimeParser.format(to, hasEndTime) + ")";
    }
}
