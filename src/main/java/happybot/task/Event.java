package happybot.task;

import java.time.LocalDateTime;

/**
 * Represents a task with a start and end date managed by HappyBot.
 */
public class Event extends DatedTask {

    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    /**
     * Creates an event task with the specified description and date range.
     *
     * @param start The event start date.
     * @param end The event end date.
     * @param description The event description.
     * @throws IllegalArgumentException If a date is null or the start is not before the end.
     */
    public Event(LocalDateTime start, LocalDateTime end, String description) {
        super(description);
        if (start == null || end == null || !start.isBefore(end)) {
            throw new IllegalArgumentException("An event must start before it ends.");
        }

        assert start.isBefore(end) : "An event must have a start time before its end time.";
        this.startTime = start;
        this.endTime = end;
    }

    /**
     * Returns the event start date.
     *
     * @return The event start date.
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Returns the event end date.
     *
     * @return The event end date.
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public boolean hasSameDetails(Task otherTask) {
        return super.hasSameDetails(otherTask)
                && startTime.equals(((Event) otherTask).startTime)
                && endTime.equals(((Event) otherTask).endTime);
    }

    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + formatDate(this.startTime)
                + " to: " + formatDate(this.endTime) + ")";
    }
}
