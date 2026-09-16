package panda.model;

import java.time.LocalDateTime;

import panda.util.DateTimeUtil;

/**
 * Represents a task that takes place during a specified time interval.
 */
public class Event extends Task {
    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Creates an event task with explicit start and end times.
     *
     * @param description task description
     * @param from event start time
     * @param to event end time
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description, TaskType.EVENT);
        this.from = from;
        this.to = to;
    }

    /**
     * Creates an event from user-entered start and end date/time strings.
     *
     * @param description task description
     * @param from event start date/time text
     * @param to event end date/time text
     */
    public Event(String description, String from, String to) {
        this(description, DateTimeUtil.parse(from), DateTimeUtil.parse(to));
    }

    /**
     * Returns the event start date/time.
     *
     * @return event start date/time
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns the event end date/time.
     *
     * @return event end date/time
     */
    public LocalDateTime getTo() {
        return to;
    }

    @Override
    public boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other) && from.equals(((Event) other).from) && to.equals(((Event) other).to);
    }

    @Override
    public String toString() {
        return super.toString() + " (from: " + DateTimeUtil.formatForDisplay(from)
                + " to: " + DateTimeUtil.formatForDisplay(to) + ")";
    }

    /**
     * Returns this event in the save-file format.
     *
     * @return one line of event data
     */
    @Override
    public String toFileString() {
        return super.toFileString() + " | " + DateTimeUtil.formatForStorage(from)
                + " | " + DateTimeUtil.formatForStorage(to);
    }
}
