package chausistant.task;

import java.time.LocalDateTime;

/** A task that occurs during a specified time interval. */
public class EventTask extends Task {
    private final LocalDateTime from;
    private final LocalDateTime to;
    private final boolean hasFromTime;
    private final boolean hasToTime;

    /** Creates an event task with its parsed start and end date/time. */
    public EventTask(String item, LocalDateTime from, boolean hasFromTime,
                     LocalDateTime to, boolean hasToTime) {
        super(item);
        // Parser and storage validate each timestamp and reject inverted event ranges.
        assert from != null && to != null : "Event tasks must have a start and end time.";
        assert !to.isBefore(from) : "An event must not end before it starts.";
        this.from = from;
        this.to = to;
        this.hasFromTime = hasFromTime;
        this.hasToTime = hasToTime;
    }

    /** Returns the event start date and time for filtering and sorting. */
    public LocalDateTime getFrom() {
        return from;
    }

    /** Returns the event end date and time for filtering. */
    public LocalDateTime getTo() {
        return to;
    }

    /** Returns whether another event has the same start and end details. */
    @Override
    protected boolean hasSameSchedulingDetails(Task other) {
        EventTask otherEvent = (EventTask) other;
        return from.equals(otherEvent.from) && to.equals(otherEvent.to)
                && hasFromTime == otherEvent.hasFromTime && hasToTime == otherEvent.hasToTime;
    }

    /** Returns this event task in the chatbot's display format. */
    @Override
    public String printTask() {
        return "[E]" + getStatusMark() + " " + getItem()
                + " (from: " + formatDateTimeForDisplay(from, hasFromTime)
                + " to: " + formatDateTimeForDisplay(to, hasToTime) + ")";
    }

    /** Returns this event task as one save-file line. */
    @Override
    public String toSaveFormat() {
        return formatSaveLine("E", getSaveStatus(), getItem(),
                formatDateTimeForSave(from, hasFromTime),
                formatDateTimeForSave(to, hasToTime));
    }
}
