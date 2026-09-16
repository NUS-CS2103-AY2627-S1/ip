package crow.task;

import java.time.LocalDateTime;

/**
 * Represents a task that occurs between a start and end date or time.
 */
public class Event extends Task {
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;

    /**
     * Creates an event task.
     *
     * @param description Description of the event.
     * @param startDateTime Start date and time.
     * @param endDateTime End date and time.
     */
    public Event(String description, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        super(description);
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    /**
     * Returns the event's start date and time.
     *
     * @return Start date and time.
     */
    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    /**
     * Returns the event's end date and time.
     *
     * @return End date and time.
     */
    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    @Override
    public boolean hasSameDetailsAs(Task other) {
        return super.hasSameDetailsAs(other)
                && startDateTime.equals(((Event) other).startDateTime)
                && endDateTime.equals(((Event) other).endDateTime);
    }

    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + startDateTime.format(DISPLAY_DATE_TIME_FORMAT)
                + " to: " + endDateTime.format(DISPLAY_DATE_TIME_FORMAT) + ")";
    }
}
