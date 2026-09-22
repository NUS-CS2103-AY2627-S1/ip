package nico;

import java.time.LocalDateTime;

/**
 * Represents a task that happens between a start date-time and an end date-time.
 */
public class Event extends Task {
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    /**
     * Creates an event task with its start and end date-times.
     *
     * @param description Event description.
     * @param startTime Event start date and time.
     * @param endTime Event end date and time.
     */
    public Event(String description, LocalDateTime startTime, LocalDateTime endTime) {
        super(description);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Returns the event's start date and time for sorting and comparison.
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Returns this event in the format used for display and storage.
     *
     * @return Formatted event text, including its completion status and time range.
     */
    @Override
    public String toString() {
        String startTimeString = startTime.format(DATE_TIME_FORMATTER);
        String endTimeString = endTime.format(DATE_TIME_FORMATTER);
        return String.format("[E]%s %s (from: %s to: %s)",
                getStatusMarker(), getDescription(), startTimeString, endTimeString);
    }
}
