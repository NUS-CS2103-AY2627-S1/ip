package oz.task;

import java.time.LocalDate;

import oz.exception.OzException;

/**
 * Represents a task that spans a start date/time and an end date/time.
 */
public class Event extends Task {
    /** Task type code used in storage and display output. */
    public static final String TYPE_CODE = "E";

    /** Error shown when an event ends before it starts. */
    private static final String START_AFTER_END_MESSAGE =
            "The start date/time (/from) cannot be after the end date/time (/to).";

    /** Start date and time of the event. */
    protected TaskDateTime startDateTime;
    /** End date and time of the event. */
    protected TaskDateTime endDateTime;

    /**
     * Constructs an Event task with description, start time, and end time.
     *
     * @param description Description of the event.
     * @param startDateTime Start date/time of the event.
     * @param endDateTime End date/time of the event.
     * @throws OzException If the start date/time is after the end date/time.
     */
    public Event(String description, TaskDateTime startDateTime,
            TaskDateTime endDateTime) throws OzException {
        super(description);
        assert startDateTime != null : "An event must have a start date-time";
        assert endDateTime != null : "An event must have an end date-time";
        if (startDateTime.isAfter(endDateTime)) {
            throw new OzException(START_AFTER_END_MESSAGE);
        }
        assert !startDateTime.isAfter(endDateTime)
                : "An event must not start after it ends";
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    /**
     * Checks if this event occurs on the given date (between start and end date inclusive).
     *
     * @param date The date to check against.
     * @return True if the event spans across the specified date.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        LocalDate startDate = this.startDateTime.toLocalDate();
        LocalDate endDate = this.endDateTime.toLocalDate();
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Converts the event task into a file storage representation.
     *
     * @return Formatted string for writing to file.
     */
    @Override
    public String toFileFormat() {
        return String.format(TYPE_CODE + " | %s | %s | %s",
                super.toFileFormat(),
                this.startDateTime.toStorageString(),
                this.endDateTime.toStorageString());
    }

    @Override
    public String toString() {
        return String.format("[" + TYPE_CODE + "][%s] %s (from: %s to: %s)",
                super.getStatusIcon(), this.description,
                this.startDateTime.toDisplayString(), this.endDateTime.toDisplayString());
    }
}
