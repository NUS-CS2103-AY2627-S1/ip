package tony.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task with a start date and an end date.
 */
public class Event extends Task {
    /** Format used when showing event dates to the user. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    /** The first date of the event. */
    private final LocalDate startDate;

    /** The last date of the event. */
    private final LocalDate endDate;

    /**
     * Creates an incomplete event task.
     *
     * @param description the text describing the event.
     * @param startDate the event start date.
     * @param endDate the event end date.
     */
    public Event(String description, LocalDate startDate, LocalDate endDate) {
        super(description);
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Event dates cannot be null");
        }
        if (!endDate.isAfter(startDate)) {
            throw new IllegalArgumentException("An event's end date must be after its start date");
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Returns the event task category.
     *
     * @return the event task type
     */
    @Override
    protected TaskType getTaskType() {
        return TaskType.EVENT;
    }

    /**
     * Returns whether the specified date is within the event's inclusive date range.
     *
     * @param date the date to check
     * @return whether the date is between the start and end dates, inclusive
     */
    @Override
    public boolean isOnDate(LocalDate date) {
        return date != null && !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Returns whether another task is an event with the same description and date range.
     *
     * @param other task to compare against.
     * @return whether the tasks represent the same event.
     */
    @Override
    public boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other)
                && startDate.equals(((Event) other).startDate)
                && endDate.equals(((Event) other).endDate);
    }

    /**
     * Formats this event as one line for storage on disk.
     *
     * @return the task fields followed by the event's start and end dates
     */
    @Override
    public String toDataString() {
        return super.toDataString() + " | " + startDate + " | " + endDate;
    }

    /**
     * Formats this event for display in the task list.
     *
     * @return the task details followed by the formatted event date range
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + startDate.format(DISPLAY_DATE_FORMAT)
                + " to: " + endDate.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
