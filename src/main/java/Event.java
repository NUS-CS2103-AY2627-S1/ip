/**
 * Represents a task that occurs between specified start and end dates or times.
 */
public class Event extends Task {
    private final String start;
    private final String end;

    /**
     * Creates an incomplete event with the given description, start, and end.
     *
     * @param description the event description.
     * @param start the date or time when the event starts.
     * @param end the date or time when the event ends.
     */
    public Event(String description, String start, String end) {
        super(description);
        this.start = start;
        this.end = end;
    }

    @Override
    protected String getTypeCode() {
        return "E";
    }

    @Override
    public String toSaveFormat() {
        return super.toSaveFormat() + " | " + start + " | " + end;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + start + " to: " + end + ")";
    }
}
