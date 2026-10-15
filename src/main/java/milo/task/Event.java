package milo.task;

import java.time.LocalDateTime;

import milo.parser.DateTimeParser;

/** A task that occurs between a start date/time and an end date/time. */
public class Event extends Task {
    /** Date and time at which the event starts. */
    private final LocalDateTime startDate;
    /** Date and time at which the event ends. */
    private final LocalDateTime endDate;

    /** Creates an event from user-entered dates/times. */
    public Event(String description, String startDate, String endDate) {
        super(description);
        this.startDate = DateTimeParser.parse(startDate);
        this.endDate = DateTimeParser.parse(endDate);
    }

    /** Creates an event from already parsed dates/times. */
    public Event(String description, LocalDateTime startDate, LocalDateTime endDate) {
        super(description);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /** Returns the start date and time. */
    public LocalDateTime getStartDate() {
        return startDate;
    }

    /** Returns the end date and time. */
    public LocalDateTime getEndDate() {
        return endDate;
    }

    /** Returns this event in the format used by storage. */
    @Override
    public String storageString() {
        return String.format("E | %s | %s | %s", super.storageString(),
                DateTimeParser.formatForStorage(startDate), DateTimeParser.formatForStorage(endDate));
    }

    /** Returns this event in the format shown to the user. */
    @Override
    public String toString() {
        return String.format("[E] %s (from: %s to: %s)", super.toString(),
                DateTimeParser.formatForDisplay(startDate), DateTimeParser.formatForDisplay(endDate));
    }
}
