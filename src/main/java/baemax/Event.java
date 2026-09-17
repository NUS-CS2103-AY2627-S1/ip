package baemax;

/**
 * A task that spans a start point and an end point in time.
 */
public class Event extends Task {
    /** When the event starts. */
    private final TaskDate from;

    /** When the event ends. */
    private final TaskDate to;

    /**
     * Creates a pending event task.
     *
     * @param description the event description
     * @param from the start date
     * @param to the end date
     */
    public Event(String description, TaskDate from, TaskDate to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Formats the event with its type, completion status, and date range.
     *
     * @return the formatted event
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }

    /**
     * Prefixes the shared fields with the event type tag {@code E} and appends
     * the start and end dates in ISO form so they can be read back.
     *
     * @return the save-file representation of this event
     */
    @Override
    public String toFileFormat() {
        return "E | " + super.toFileFormat() + " | " + from.toStorageString() + " | " + to.toStorageString();
    }

    /**
     * Returns an independent copy of this event.
     *
     * @return a new event with the same description, date range, and done state
     */
    @Override
    public Task copy() {
        Event copy = new Event(getDescription(), from, to);
        copyDoneStatusInto(copy);
        return copy;
    }
}
