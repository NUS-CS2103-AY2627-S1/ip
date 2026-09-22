package genie.model;

import java.time.LocalDateTime;

import genie.util.DateFormats;

/** Represents a task that starts and ends at specific times. */
public class Event extends Task {
    protected LocalDateTime from;
    protected LocalDateTime to;

    /**
     * Initializes a new Event task.
     *
     * @param description Description of the event.
     * @param from        Start date or time of the event.
     * @param to          End date or time of the event.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        assert from != null : "Event start date and time must not be null";
        assert to != null : "Event end date and time must not be null";
        this.from = from;
        this.to = to;
    }

    /** Updates the event start date and time. */
    public void setFrom(LocalDateTime from) {
        assert from != null : "Event start date and time must not be null";
        this.from = from;
    }

    /** Updates the event end date and time. */
    public void setTo(LocalDateTime to) {
        assert to != null : "Event end date and time must not be null";
        this.to = to;
    }

    /** Returns the event start date and time. */
    public LocalDateTime getFrom() {
        return from;
    }

    /** Returns the event end date and time. */
    public LocalDateTime getTo() {
        return to;
    }

    /** Returns the event in the format used for file storage. */
    @Override
    public String toFileFormat() {
        return "E | " + super.toFileFormat() + " | " + from.format(DateFormats.INPUT_FORMAT)
                + " | " + to.format(DateFormats.INPUT_FORMAT);
    }

    /** Returns the event with its formatted start and end times. */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from.format(DateFormats.OUTPUT_FORMAT)
                + " to: " + to.format(DateFormats.OUTPUT_FORMAT) + ")";
    }
}
