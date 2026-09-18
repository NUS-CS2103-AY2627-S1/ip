package chatbot;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * Represents a task that lasts from a given start date/time to a given end date/time.
 */
public class Event extends Task {
    private final LocalDateTime start;
    private final LocalDateTime end;

    /**
     * Takes in given task description, start and end time and creates the object.
     *
     * @param description Event description.
     * @param startString Start time in {@code yyyy-MM-dd HHmm} format.
     * @param endString End time in {@code yyyy-MM-dd HHmm} format.
     */
    public Event(String description, String startString, String endString) {
        super(description);
        if (startString == null || startString.isBlank()
                || endString == null || endString.isBlank()) {
            throw new InvalidInputException("Hey bro you left the datetime blank after the commands. You gotta"
                + " input the datetimes in this format:\n" + DateTimeFormats.INPUT_FORMAT);
        }
        try {
            this.start = LocalDateTime.parse(startString, DateTimeFormats.STORAGE);
            this.end = LocalDateTime.parse(endString, DateTimeFormats.STORAGE);
            if (this.end.isBefore(this.start)) {
                throw new InvalidInputException("Bro your event end time cannot be before its start time.");
            }
        } catch (DateTimeParseException e) {
            throw new InvalidInputException("Hey bro you gave the datetime in an invalid format,"
                + " you gotta input the datetimes in this format:\n" + DateTimeFormats.INPUT_FORMAT);
        }

        if (this.end.isBefore(this.start)) {
            throw new InvalidInputException("Hey bro your event's end time cannot be before its start time,"
                + " please try again :)");
        }

        // Events are expected to describe a time interval, not an interval running backwards.
        assert this.start != null && this.end != null : "An event must have parsed start and end times";
        assert !this.end.isBefore(this.start) : "An event must end at or after it starts";
    }

    /**
     * Returns the date and time at which this event starts.
     *
     * @return This event's start date and time.
     */
    public LocalDateTime getStart() {
        return this.start;
    }

    /**
     * Returns the date and time at which this event ends.
     *
     * @return This event's end date and time.
     */
    public LocalDateTime getEnd() {
        return this.end;
    }

    /**
     * Checks if a given event is a duplicate of current instance.
     *
     * @param e Event to be checked against current instance.
     * @return True if input event has same start duration, end duration and same task description as current object,
     *     False otherwise.
     */
    public boolean isDuplicate(Event e) {
        return super.hasSameDescription(e.getDescription())
            && this.start.equals(e.getStart())
            && this.end.equals(e.getEnd());
    }

    /**
     * Returns the event's type marker, status, description, and time range.
     *
     * @return Formatted event description.
     */
    @Override
    public String toString() {
        return "[E] " + this.getCompletionStatus() + " " + this.getDescription()
            + " (from: " + start.format(DateTimeFormats.DISPLAY_DATE_TIME) + " to: "
                + end.format(DateTimeFormats.DISPLAY_DATE_TIME) + ")";
    }
}
