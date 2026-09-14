package groot.task;

/**
 * Represents a task that occurs between a given start and end date or time.
 */
public class Event extends Task {
    /** Start date or time supplied for the event. */
    protected String start;

    /** End date or time supplied for the event. */
    protected String end;

    /**
     * Creates an incomplete event task.
     *
     * @param description Description of the event.
     * @param start Start date or time of the event.
     * @param end End date or time of the event.
     */
    public Event(String description, String start, String end) {
        super(description);
        this.start = start;
        this.end = end;
    }

    /**
     * Returns the event's start text.
     */
    public String getStart() {
        return start;
    }

    /**
     * Returns the event's end text.
     */
    public String getEnd() {
        return end;
    }

    /**
     * Returns the event in the format used by the local data file.
     *
     * @return Pipe-separated event data.
     */
    @Override
    public String toDataString() {
        return "E | " + super.toDataString() + " | " + escapeDataField(start)
                + " | " + escapeDataField(end);
    }

    /**
     * Returns the event in its display format.
     *
     * @return Formatted event text.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + start + " to: " + end + ")";
    }
}
