package wwaffle.task;

/**
 * Represents a task that occurs between specified start and end times.
 */
public class Event extends Task {
    private final String start;
    private final String end;

    /**
     * Creates an incomplete event with the given description and times.
     *
     * @param description Description of the event.
     * @param from Start date or time, stored as text.
     * @param to End date or time, stored as text.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.start = from;
        this.end = to;
    }

    /**
     * Returns the event's start date or time.
     *
     * @return Start date or time.
     */
    public String getStart() {
        return start;
    }

    /**
     * Returns the event's end date or time.
     *
     * @return End date or time.
     */
    public String getEnd() {
        return end;
    }

    /**
     * Returns the event in its display format.
     *
     * @return Formatted event.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (" + start + " - " + end + ")";
    }
}
