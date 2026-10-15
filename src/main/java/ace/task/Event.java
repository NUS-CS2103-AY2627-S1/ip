package ace.task;

/**
 * A task that spans a start time and an end time. Unlike {@link Deadline}, the
 * start and end times are kept as free-form text rather than parsed dates, so any
 * value the user types is accepted as-is.
 */
public class Event extends Task {
    private final String startTime;
    private final String endTime;

    /**
     * Creates a new, incomplete event with the given description, start time, and
     * end time.
     *
     * @param description the text describing what this event is.
     * @param startTime the event's start time, as free-form text.
     * @param endTime the event's end time, as free-form text.
     */
    public Event(String description, String startTime, String endTime) {
        super(description);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Returns this event's start time.
     *
     * @return the event's start time, as free-form text.
     */
    public String getStartTime() {
        return this.startTime;
    }

    /**
     * Returns this event's end time.
     *
     * @return the event's end time, as free-form text.
     */
    public String getEndTime() {
        return this.endTime;
    }

    /**
     * Returns the representation of this event used when saving it to disk. An
     * event has no separate machine-readable format from its display form, so this
     * is identical to {@link #toString()}.
     *
     * @return the save-file representation of this event.
     */
    @Override
    public String loadFormat() {
        return this.toString();
    }

    /**
     * Returns this event's display representation, e.g.
     * "[E][ ] meeting (from: Mon 2pm to: 4pm)".
     *
     * @return this event formatted for display.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + this.getStartTime() + " to: " + this.getEndTime() + ")";
    }
}
