package swell.task;

import java.util.ArrayList;

/**
 * Represents a task that happens over a period of time.
 */
public class Event extends Task {
    private final TaskDateTime from;
    private final TaskDateTime to;

    /**
     * Creates an event task with the given description, start, and end date-time.
     *
     * @param description task description.
     * @param from event start date-time.
     * @param to event end date-time.
     */
    public Event(String description, TaskDateTime from, TaskDateTime to) {
        super(TaskType.EVENT, description);
        this.from = from;
        this.to = to;
    }

    /**
     * Creates an event task with the given description, start, end, and tags.
     *
     * @param description task description.
     * @param from event start date-time.
     * @param to event end date-time.
     * @param tags task tags.
     */
    public Event(String description, TaskDateTime from, TaskDateTime to, ArrayList<String> tags) {
        super(TaskType.EVENT, description, tags);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event start date-time.
     *
     * @return event start date-time.
     */
    public TaskDateTime getFrom() {
        return from;
    }

    /**
     * Returns the event end date-time.
     *
     * @return event end date-time.
     */
    public TaskDateTime getTo() {
        return to;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + from.toDisplayString()
                + " to: " + to.toDisplayString() + ")";
    }
}
