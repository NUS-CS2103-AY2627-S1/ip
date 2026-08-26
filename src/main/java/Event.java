/**
 * Represents a task that occurs between a specified start and end date, optionally with times.
 */
public class Event extends Task {
    private final TaskDateTime start;
    private final TaskDateTime end;

    /**
     * Creates an incomplete event with the given description, start, and end.
     *
     * @param description the event description.
     * @param start the date (and optional time) when the event starts.
     * @param end the date (and optional time) when the event ends.
     */
    public Event(String description, TaskDateTime start, TaskDateTime end) {
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
        return super.toSaveFormat() + " | " + start.toSaveFormat() + " | " + end.toSaveFormat();
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + start + " to: " + end + ")";
    }
}
