package arin.task;

/**
 * Represents a task that occurs between a start and end time.
 */
public class Event extends Task {
    private final String start;
    private final String end;

    /**
     * Creates an event task with a start and end time description.
     *
     * @param description Text describing the event.
     * @param start Event start time.
     * @param end Event end time.
     */
    public Event(String description, String start, String end) {
        super(description);
        assert start != null && !start.trim().isEmpty() : "Event start times must be validated before construction";
        assert end != null && !end.trim().isEmpty() : "Event end times must be validated before construction";
        this.start = start;
        this.end = end;
    }

    /**
     * Returns this event in the storage-file format.
     *
     * @return Storage text prefixed with the event task type and times.
     */
    @Override
    public String storageToString() {
        return "E" + super.storageToString() + " | " + this.start + " | " + this.end;
    }

    /**
     * Returns this event in the display format.
     *
     * @return Display text prefixed with the event task type and times.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + this.start + " to: " + this.end + ")";
    }
}
