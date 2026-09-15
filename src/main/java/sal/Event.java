package sal;

/**
 * A task that spans a start and end date/time.
 */
public class Event extends Task {
    protected TaskDateTime from;
    protected TaskDateTime to;

    /**
     * Creates an event with the given description and start/end date/times.
     *
     * @param description Text describing the event.
     * @param from Start date or date-time.
     * @param to End date or date-time.
     */
    public Event(String description, TaskDateTime from, TaskDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return withTags("[E]" + super.toString()
                + " (from: " + DateTimeParser.formatForDisplay(from)
                + " to: " + DateTimeParser.formatForDisplay(to) + ")");
    }
}
