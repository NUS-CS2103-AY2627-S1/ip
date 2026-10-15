package chocolate.task;

/**
 * Represents a task with start and end details.
 */
public class Event extends Task {
    /** Start details of this event. */
    private final String start;
    /** End details of this event. */
    private final String end;

    /**
     * Creates an event with the specified description and schedule.
     *
     * @param description Description of the task.
     * @param start Start details.
     * @param end End details.
     */
    public Event(String description, String start, String end) {
        super(description);
        this.start = start;
        this.end = end;
    }

    /**
     * Returns the event start details.
     *
     * @return Start details.
     */
    public String getStart() {
        return start;
    }

    /**
     * Returns the event end details.
     *
     * @return End details.
     */
    public String getEnd() {
        return end;
    }

    /**
     * Returns the display form of this event.
     *
     * @return Event type, status, description, and schedule.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + start + " to: " + end + ")";
    }
}
