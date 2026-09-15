package tianyi.task;

/**
 * Represents a task occurring between dates with optional times.
 */
public class Event extends Task {
    /**
     * Storage and display marker identifying an event.
     */
    public static final String TYPE_MARKER = "E";

    private final TaskTime startTime;
    private final TaskTime endTime;

    /**
     * Creates an incomplete event with its inclusive start and end dates.
     *
     * @param description Description of the event.
     * @param startTime Start date and optional time.
     * @param endTime End date and optional time.
     */
    public Event(String description, TaskTime startTime, TaskTime endTime) {
        super(description);

        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Reports whether this event's date range contains the specified date.
     *
     * @param taskTime Date to check.
     * @return {@code true} if the date lies within the inclusive event range.
     */
    @Override
    public boolean isOccurringOn(TaskTime taskTime) {
        return !startTime.isAfter(taskTime) && !endTime.isBefore(taskTime);
    }

    /**
     * Serializes this event for storage.
     *
     * @return Storage representation including the event type and date range.
     */
    @Override
    public String getData() {
        return TYPE_MARKER + DATA_SEPARATOR + super.getData()
                + DATA_SEPARATOR + startTime.getData()
                + DATA_SEPARATOR + endTime.getData();
    }

    /**
     * Formats this event for display.
     *
     * @return User-facing representation including the start and end values.
     */
    @Override
    public String toString() {
        return "[" + TYPE_MARKER + "]" + super.toString()
                + " (from: " + startTime + " to: " + endTime + ")";
    }
}
