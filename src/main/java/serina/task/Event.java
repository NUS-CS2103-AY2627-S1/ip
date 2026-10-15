package serina.task;

import java.time.LocalDate;

import serina.parser.DateParser;

/**
 * Represents a task that happens from a start date or time to an end date or time.
 */
public class Event extends Task {
    private final LocalDate startDate;
    private final LocalDate endDate;

    /**
     * Creates an event task with the given description, start, and end.
     *
     * @param description Text describing the task.
     * @param startDate Start date or time.
     * @param endDate End date or time.
     */
    public Event(String description, LocalDate startDate, LocalDate endDate) {
        this(description, startDate, endDate, TaskStatus.NOT_DONE);
    }

    /**
     * Creates an event task with the given description, start, end, and saved status.
     *
     * @param description Text describing the task.
     * @param startDate Start date or time.
     * @param endDate End date or time.
     * @param status Saved completion status.
     */
    public Event(String description, LocalDate startDate, LocalDate endDate, TaskStatus status) {
        super(description, status);
        assert startDate != null : "Event start dates should be parsed before event construction.";
        assert endDate != null : "Event end dates should be parsed before event construction.";
        assert endDate.isAfter(startDate) : "Event end dates should be after start dates.";

        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Returns the event task type.
     *
     * @return {@link TaskType#EVENT}.
     */
    @Override
    public TaskType getTaskType() {
        return TaskType.EVENT;
    }

    @Override
    public Task copy() {
        return new Event(getDescription(), startDate, endDate, getStatus());
    }

    /**
     * Returns the event's start and end dates formatted for display.
     *
     * @return Display text containing the event date range.
     */
    @Override
    protected String getDetails() {
        return " (from: " + DateParser.formatDisplayDate(startDate)
                + " to: " + DateParser.formatDisplayDate(endDate) + ")";
    }

    /**
     * Returns the event date fields used in the save file.
     *
     * @return Serialized event details.
     */
    @Override
    protected String getFileDetails() {
        return " | " + DateParser.formatFileDate(startDate) + " | " + DateParser.formatFileDate(endDate);
    }

    /**
     * Checks whether the given date falls within this event's inclusive date range.
     *
     * @param date Date to check.
     * @return {@code true} if {@code date} is between the start and end dates, inclusive.
     */
    @Override
    public boolean isOccurringOn(LocalDate date) {
        assert date != null : "Date searches should provide a date to compare against.";
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }
}
