package es;

import java.time.LocalDateTime;

/**
 * Represents a task that begins and ends at specified times.
 */
public class Event extends Task {
    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Creates an event task.
     *
     * @param description the text describing the event
     * @param from the event start time text
     * @param to the event end time text
     */
    public Event(String description, String from, String to) throws EsException {
        super(description, TaskType.EVENT);
        this.from = DateTimeParser.parse(from);
        this.to = DateTimeParser.parse(to);
        if (!this.to.isAfter(this.from)) {
            throw new EsException("Did you mix up the start and end time?");
        }
    }

    /**
     * Returns the encoded event line for saving to disk.
     *
     * @return a line in the form {@code E | 0 | description | from | to}
     */
    @Override
    public String toStorageLine() {
        return toStoragePrefix() + " | " + from + " | " + to;
    }

    /**
     * Returns the formatted event task.
     *
     * @return the task with its start and end times
     */
    @Override
    public String toString() {
        return super.toString()
                + " (from: " + DateTimeParser.format(from) + " to: " + DateTimeParser.format(to) + ")";
    }
}
