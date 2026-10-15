package alex.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import alex.util.DateParser;
import alex.util.TaskDateTime;

/**
 * Represents a task that occurs over a date range.
 */
public class Event extends Task {
    private final TaskDateTime startDateTime;
    private final TaskDateTime endDateTime;

    /**
     * Creates an event with a description and date range.
     *
     * @param description description of the event.
     * @param startDate event start date.
     * @param endDate event end date.
     */
    public Event(String description, LocalDate startDate, LocalDate endDate) {
        this(description, new TaskDateTime(startDate), new TaskDateTime(endDate));
    }

    /**
     * Creates an event with a description and date range containing optional times.
     * A missing start time means the start of its date; a missing end time means the end of its date.
     *
     * @param description description of the event.
     * @param startDateTime event start date and optional time.
     * @param endDateTime event end date and optional time.
     */
    public Event(String description, TaskDateTime startDateTime, TaskDateTime endDateTime) {
        super(description);
        assert startDateTime != null : "Event start date and time cannot be null";
        assert endDateTime != null : "Event end date and time cannot be null";

        LocalDateTime effectiveStartDateTime = LocalDateTime.of(
                startDateTime.date(), startDateTime.time().orElse(LocalTime.MIN));
        LocalDateTime effectiveEndDateTime = LocalDateTime.of(
                endDateTime.date(), endDateTime.time().orElse(LocalTime.MAX));
        if (!effectiveEndDateTime.isAfter(effectiveStartDateTime)) {
            throw new IllegalArgumentException(
                    "The event must end after it starts. We serve coffee, not temporal paradoxes.");
        }

        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toDataString() {
        return "E | " + getDoneFlag() + " | " + getDescription()
                + " | " + DateParser.formatForStorage(startDateTime)
                + " | " + DateParser.formatForStorage(endDateTime);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + DateParser.format(startDateTime)
                + " to: " + DateParser.format(endDateTime) + ")";
    }
}
