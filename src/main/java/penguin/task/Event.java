package penguin.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import penguin.enums.TaskType;
import penguin.util.DateTimeUtil;

/**
 * Represents a task that starts and ends at specified date or time values.
 */
public final class Event extends Task {
    /** Start date or time of the event. */
    private final LocalDateTime startDateTime;

    /** End date or time of the event. */
    private final LocalDateTime endDateTime;

    /**
     * Creates an event with a description, start time, and end time.
     *
     * @param description description of the event.
     * @param eventStartDateTime date or time when the event starts.
     * @param eventEndDateTime date or time when the event ends.
     */
    public Event(String description, LocalDateTime eventStartDateTime,
            LocalDateTime eventEndDateTime) {
        super(description, TaskType.EVENT);
        this.startDateTime = eventStartDateTime;
        this.endDateTime = eventEndDateTime;
    }

    /**
     * Creates an independent copy of this event.
     *
     * @return copy with the same description, times, and completion status.
     */
    @Override
    public Task copy() {
        Event copy = new Event(getDescription(), startDateTime, endDateTime);
        copyCompletionStatusTo(copy);
        return copy;
    }

    /**
     * Returns the event in persistent storage format.
     *
     * @return serialized event data.
     */
    @Override
    public String toStorageFormat() {
        return String.format("%s | %s | %s", super.toStorageFormat(),
                DateTimeUtil.formatForStorage(startDateTime),
                DateTimeUtil.formatForStorage(endDateTime));
    }

    /**
     * Checks whether the event spans the specified date.
     *
     * @param date date to check.
     * @return true if the event occurs on the specified date.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(startDateTime.toLocalDate())
                && !date.isAfter(endDateTime.toLocalDate());
    }

    /**
     * Returns the event formatted for display.
     *
     * @return formatted event description and date/time range.
     */
    @Override
    public String toString() {
        return super.toString() + " (from: "
                + DateTimeUtil.formatForDisplay(startDateTime) + " to: "
                + DateTimeUtil.formatForDisplay(endDateTime) + ")";
    }
}
