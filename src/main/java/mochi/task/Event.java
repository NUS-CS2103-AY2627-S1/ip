package mochi.task;

import java.time.LocalDateTime;

import mochi.DateTimeUtil;

/**
 * Represents a task occurring during a specified period.
 */
public class Event extends Task {
    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Creates an event occurring between the given date-times.
     *
     * @param description Description of the event.
     * @param from Start date and time.
     * @param to End date and time.
     * @throws IllegalArgumentException If the event does not end after it starts.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        assert from != null : "Event start date-time must not be null";
        assert to != null : "Event end date-time must not be null";

        if (!to.isAfter(from)) {
            throw new IllegalArgumentException("event end must be after its start");
        }
        assert to.isAfter(from) : "Validated event must end after it starts";

        this.from = from;
        this.to = to;
    }

    /**
     * Converts this event into the line format used in the data file.
     *
     * @return Serialized event.
     */
    @Override
    public String toDataString() {
        return formatDataString("E") + " | " + DateTimeUtil.formatForStorage(from)
                + " | " + DateTimeUtil.formatForStorage(to);
    }

    /**
     * Returns this event in its user-facing display format.
     *
     * @return Formatted event.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + String.format(" (from: %s to: %s)",
                        DateTimeUtil.formatForDisplay(from), DateTimeUtil.formatForDisplay(to));
    }
}
