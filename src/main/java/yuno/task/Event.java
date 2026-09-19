package yuno.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import yuno.util.DateTimeFormats;

/**
 * Represents a task that starts and ends at specified times.
 */
public class Event extends Task {
    /** Stores the date and time at which the event starts. */
    private final LocalDateTime startDateTime;
    /** Stores the date and time at which the event ends. */
    private final LocalDateTime endDateTime;

    /**
     * Creates an event task with the specified description, status, start time, and end time.
     *
     * @param description Text that describes the task.
     * @param isDone Whether the task is completed.
     * @param startDateTime Date and time at which the event starts.
     * @param endDateTime Date and time at which the event ends.
     */
    public Event(String description, boolean isDone, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        super(description, isDone);
        assert startDateTime != null : "Event start date-time must not be null";
        assert endDateTime != null : "Event end date-time must not be null";
        assert startDateTime.isBefore(endDateTime) : "Event must end after it starts";
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    /**
     * Returns the date and time at which this event starts.
     *
     * @return Event start date and time.
     */
    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    /**
     * Returns the date and time at which this event ends.
     *
     * @return Event end date and time.
     */
    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    @Override
    public boolean isRelevantFor(LocalDate date) {
        return !date.isBefore(getStartDateTime().toLocalDate())
                && !date.isAfter(getEndDateTime().toLocalDate());
    }

    @Override
    public String toStorageString() {
        return String.format(
                "E | %c | %s | %s | %s",
                getStatus(),
                getDescription(),
                DateTimeFormats.STORAGE_FORMATTER.format(getStartDateTime()),
                DateTimeFormats.STORAGE_FORMATTER.format(getEndDateTime()));
    }

    @Override
    public LocalDateTime getChronologicalTime() {
        return getStartDateTime();
    }

    @Override
    Task copy() {
        return new Event(
                getDescription(), getStatus() == 'X', getStartDateTime(), getEndDateTime());
    }

    /**
     * Returns this event task in a display-ready format.
     *
     * @return Formatted event task description, status, start time, and end time.
     */
    @Override
    public String toString() {
        return String.format(
                "[E]%s (from: %s to: %s)",
                super.toString(),
                DateTimeFormats.DISPLAY_FORMATTER.format(getStartDateTime()),
                DateTimeFormats.DISPLAY_FORMATTER.format(getEndDateTime()));
    }
}
