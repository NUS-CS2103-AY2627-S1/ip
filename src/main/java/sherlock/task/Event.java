package sherlock.task;

/**
 * Represents a task that occurs during a specified time period.
 */
public class Event extends Task {
    private final String startTime;
    private final String endTime;

    /**
     * Creates an incomplete task that occurs during the given period.
     *
     * @param description text describing the task
     * @param startTime event start text
     * @param endTime event end text
     */
    public Event(String description, String startTime, String endTime) {
        super(description);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    protected String getTypeIcon() {
        return "E";
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return super.toString() + " (from: " + startTime + " to: " + endTime + ")";
    }

    /** {@inheritDoc} */
    @Override
    public String toFileString() {
        return super.toFileString() + " | " + startTime + " | " + endTime;
    }
}
