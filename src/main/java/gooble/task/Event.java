package gooble.task;

/**
 * Represents a task scheduled between a start and end date or time.
 */
public class Event extends Task {
    /** The start date or time of this event. */
    private final String startDate;

    /** The end date or time of this event. */
    private final String endDate;

    /**
     * Creates an incomplete event with a description, start date, and end date.
     *
     * @param description the text describing the event
     * @param startDate the event's start date or time
     * @param endDate the event's end date or time
     */
    public Event(String description, String startDate, String endDate) {
        super(description);
        if (isBlank(startDate) || isBlank(endDate)) {
            throw new IllegalArgumentException("Event times cannot be empty.");
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /** Returns whether an event time is absent or contains only whitespace. */
    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    /**
     * Returns the event's start text.
     *
     * @return the start date or time
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * Returns the event's end text.
     *
     * @return the end date or time
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * Returns the formatted representation used for an event.
     *
     * @return the task type, completion status, description, start date, and end date
     */
    @Override
    public String toString() {
        return "[E][" + getStatusIcon() + "] " + description + formatTags()
                + " (from: " + startDate + " to: " + endDate + ")";
    }
}
