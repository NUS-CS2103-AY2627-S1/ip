package kojispawn.task;

/**
 * Represents a task that takes place between specified start and end times.
 */
public class Event extends Task {
    private final String dateFrom;
    private final String dateTo;

    /**
     * Creates an incomplete event.
     *
     * @param description Description of the event.
     * @param dateFrom Date or time at which the event starts.
     * @param dateTo Date or time at which the event ends.
     */
    public Event(String description, String dateFrom, String dateTo) {
        super(description);
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }

    @Override
    Task copy() {
        Event copy = new Event(description, dateFrom, dateTo);
        copyCompletionStatusTo(copy);
        return copy;
    }

    @Override
    public String toDataString() {
        return "E | " + (isDone ? "1" : "0") + " | " + description
                + " | " + dateFrom + " | " + dateTo;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + dateFrom + " to: " + dateTo + ")";
    }
}
