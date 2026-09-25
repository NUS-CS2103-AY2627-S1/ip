package shan.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import shan.datetime.DateTimeParser;

/**
 * Represents a task that occurs over a period of time.
 */
public class Event extends Task {
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    /**
     * Constructs an event with start and end date-times.
     *
     * @param taskName  Name of the task.
     * @param startDate Start date and time of the event.
     * @param endDate   End date and time of the event.
     */
    public Event(String taskName, LocalDateTime startDate, LocalDateTime endDate) {
        super(taskName);
        assert startDate != null : "Event start date must not be null";
        assert endDate != null : "Event end date must not be null";
        assert endDate.isAfter(startDate) : "Event end date must be after its start date";
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Returns this Event in the save-file format.
     *
     * @return Serialized event.
     */
    @Override
    public String toFileString() {
        return String.format("E | %s | %s | %s", getFileFields(),
                DateTimeParser.formatForStorage(this.startDate),
                DateTimeParser.formatForStorage(this.endDate));
    }

    /**
     * Returns whether this event overlaps an inclusive date range.
     *
     * @param startDate First date in the range.
     * @param endDate   Last date in the range.
     * @return {@code true} when any part of the event occurs within the range.
     */
    @Override
    public boolean occursBetween(LocalDate startDate, LocalDate endDate) {
        LocalDate eventStartDate = this.startDate.toLocalDate();
        LocalDate eventEndDate = this.endDate.toLocalDate();
        return !eventEndDate.isBefore(startDate) && !eventStartDate.isAfter(endDate);
    }

    /**
     * Returns the display representation of this event.
     *
     * @return Display representation of this event.
     */
    @Override
    public String toString() {
        return String.format("[E]%s (from: %s to: %s)", super.toString(),
                DateTimeParser.formatForDisplay(this.startDate),
                DateTimeParser.formatForDisplay(this.endDate));
    }
}
