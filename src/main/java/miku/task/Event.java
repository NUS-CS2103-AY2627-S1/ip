package miku.task;

import java.time.LocalDateTime;

import miku.parser.DateTimeParser;

/** A task that starts and ends at specified dates or times. */
public class Event extends Task {
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;
    private final boolean hasStartTime;
    private final boolean hasEndTime;

    /** Creates an undone event with its start and end date or time. */
    public Event(String description, LocalDateTime startDateTime, boolean hasStartTime,
            LocalDateTime endDateTime, boolean hasEndTime) {
        super(TaskType.EVENT, description);
        assert startDateTime != null : "Events must have a start date and time.";
        assert endDateTime != null : "Events must have an end date and time.";
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.hasStartTime = hasStartTime;
        this.hasEndTime = hasEndTime;
    }

    /** Returns the event's typed start date and time. */
    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    /** Returns the event's typed end date and time. */
    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    /** Returns whether the event start was entered with an explicit time. */
    public boolean hasStartTime() {
        return hasStartTime;
    }

    /** Returns whether the event end was entered with an explicit time. */
    public boolean hasEndTime() {
        return hasEndTime;
    }

    /** Returns this event in Miku's task-list display format. */
    @Override
    public String toString() {
        return super.toString() + " (from: " + DateTimeParser.format(startDateTime, hasStartTime)
                + " to: " + DateTimeParser.format(endDateTime, hasEndTime) + ")";
    }
}
