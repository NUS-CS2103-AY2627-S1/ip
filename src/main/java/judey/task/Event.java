package judey.task;

import judey.exception.JudeyException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents a task with an event duration - start and end time
 */
public class Event extends Task {
    protected LocalDateTime from;
    protected LocalDateTime to;

    /**
     * Constructs a {@code Event} task with a description and target date/time from and to
     * @param desc The description of the event task
     * @param from The date/time at which the event begins; will be parsed into {@code LocalDateTime}
     * @param to The date/time at which the event ends; will be parsed into {@code LocalDateTime}
     * @throws JudeyException If the given date/time string cannot be parsed using supported formats
     */
    public Event(String desc, String from, String to) throws JudeyException {
        super(desc);
        this.from = DateTimeUtil.parse(from);
        this.to = DateTimeUtil.parse(to);
        validateTimeRange();
        assert this.from != null && this.to != null
                : "parseDateTime() either returns a valid LocalDateTime or throws JudeyException; 'from' and 'to' "
                + "should never be null once this constructor completes successfully";
    }

    /** Ensures that an event has a positive duration. */
    private void validateTimeRange() throws JudeyException {
        if (!this.from.isBefore(this.to)) {
            throw new JudeyException("The event start date/time must be before its end date/time.");
        }
    }

    /**
     * Validates that a newly created event does not begin in the past.
     *
     * @param currentDateTime the current local date/time, supplied by the caller
     * @throws JudeyException if the event has already started
     */
    public void validateStartIsNotPast(LocalDateTime currentDateTime) throws JudeyException {
        if (this.from.isBefore(currentDateTime)) {
            throw new JudeyException("The event start date/time cannot be in the past.");
        }
    }

    /**
     * Getter for the date and time at which the event begins
     * @return {@code LocalDateTime} start time
     */
    public LocalDateTime getFrom() {
        return this.from;
    }

    /**
     * Returns the date and time at which the event ends.
     *
     * @return event end time
     */
    public LocalDateTime getTo() {
        return this.to;
    }

    @Override
    public String toFileString() {
        return TYPE_EVENT + " | " + (isDone ? DONE_MARKER : NOT_DONE_MARKER) + " | "
                + description + " | " + from + " | " + to;
    }

    @Override
    public String toString() {
        String display = "[" + TYPE_EVENT + "]" + super.toString();
        display += " (from: " + DateTimeUtil.format(from) + " to: " + DateTimeUtil.format(to) + ")\n";
        return display;
    }
}
