package min.task;

/**
 * Represents a task that occurs between a specified start and end time.
 */
public class Event extends Task {

    private final String startTime;
    private final String endTime;

    /**
     * Creates an incomplete event with the given description and times.
     *
     * @param description The event description.
     * @param startTime The event's start time. Must not be null, blank, or contain the
     *                  storage field separator.
     * @param endTime The event's end time. Must not be null, blank, or contain the
     *                storage field separator.
     */
    public Event(String description, String startTime, String endTime) {
        super(description);
        assert startTime != null && !startTime.isBlank()
                : "Event start time must not be blank.";
        assert !startTime.contains(FILE_FIELD_SEPARATOR)
                : "Event start time must not contain the storage field separator.";
        assert endTime != null && !endTime.isBlank()
                : "Event end time must not be blank.";
        assert !endTime.contains(FILE_FIELD_SEPARATOR)
                : "Event end time must not contain the storage field separator.";

        this.startTime = startTime;
        this.endTime = endTime;
    }

    /** Returns this event in the format used for persistent storage. */
    @Override
    public String toFileString() {
        return "E" + FILE_FIELD_SEPARATOR + getStatusValue() + FILE_FIELD_SEPARATOR
                + getDescription() + FILE_FIELD_SEPARATOR + startTime + FILE_FIELD_SEPARATOR + endTime;
    }

    /** Returns this event in a format suitable for display. */
    @Override
    public String toString() {
        return String.format("[E]%s (from: %s to: %s)", super.toString(), startTime, endTime);
    }
}
