package samantha.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import samantha.exception.InputException;
import samantha.exception.TaskValidationException;

/**
 * Represents a task scheduled between a start and end date and time.
 */
public class Event extends Task {

    private final DateTimeValue from;
    private final DateTimeValue to;

    /**
     * Creates an event after validating its description and schedule.
     *
     * @param name event description
     * @param from raw value after {@code /from}
     * @param to raw value after {@code /to}
     * @throws TaskValidationException if the description or schedule lacks a required value,
     *         or if the end is not after the start
     * @throws InputException if a date or time value is invalid
     */
    public Event(String name, String from, String to)
            throws TaskValidationException, InputException {
        super(name, "event");
        if (from.isBlank()) {
            throw new TaskValidationException("You need to specify a start time after /from.");
        }
        if (to.isBlank()) {
            throw new TaskValidationException("You need to specify an end time after /to.");
        }
        this.from = parseEventDateTime(from);
        this.to = parseEventDateTime(to);
        assert this.from.getTime().isPresent() : "An event start must include a time";
        assert this.to.getTime().isPresent() : "An event end must include a time";
        if (!isEndAfterStart()) {
            throw new TaskValidationException("The event must end after it starts.");
        }
    }

    /**
     * Parses one endpoint of an event schedule and requires it to contain a time.
     *
     * @param text raw schedule endpoint
     * @return parsed date and time
     * @throws TaskValidationException if the endpoint does not include a time
     * @throws InputException if the endpoint has an invalid format
     */
    private DateTimeValue parseEventDateTime(String text)
            throws TaskValidationException, InputException {
        DateTimeValue dateTime = DateTimeValue.parse(text);
        if (dateTime.getTime().isEmpty()) {
            throw new TaskValidationException(
                    "An event date and time must include a time in HHmm format.");
        }
        return dateTime;
    }

    /**
     * Returns whether this event's end date-time is strictly after its start.
     *
     * @return {@code true} when the schedule has a positive duration
     */
    private boolean isEndAfterStart() {
        LocalDateTime start = LocalDateTime.of(from.getDate(), from.getTime().orElseThrow());
        LocalDateTime end = LocalDateTime.of(to.getDate(), to.getTime().orElseThrow());
        return end.isAfter(start);
    }

    /**
     * Returns the display marker for an event task.
     *
     * @return the event marker
     */
    @Override
    public String getType() {
        return "[E]";
    }

    /**
     * Returns whether the supplied date falls within this event's inclusive date range.
     *
     * @param date date to check
     * @return {@code true} when the event occurs on {@code date}
     */
    @Override
    public boolean isOnDate(LocalDate date) {
        LocalDate fromDate = from.getDate();
        LocalDate toDate = to.getDate();
        return !date.isBefore(fromDate) && !date.isAfter(toDate);
    }

    /**
     * Returns this event in the user-facing task format.
     *
     * @return formatted event task
     */
    @Override
    public String toString() {
        return String.format("%s (from: %s to: %s)", super.toString(), this.from, this.to);
    }

    /**
     * Returns this event as one record for the task data file.
     *
     * @return serialized event task
     */
    @Override
    public String toFileString() {
        String schedule = from.toStorageString() + " to " + to.toStorageString();
        return String.format("E | %d | %s | %s", getCompletionStatusCode(), getTaskName(), schedule);
    }
}
