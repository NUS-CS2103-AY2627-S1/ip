package florkofcows.task;

import java.time.LocalDate;

/**
 * Represents a task that occurs over a period of time, with a start and end.
 * Each of the start and end may independently be a recognized date/datetime
 * or free-form text, depending on what was parsed by {@link FlorkDateTime}.
 */
public class Event extends Task {
    protected FlorkDateTime from;
    protected FlorkDateTime to;

    /**
     * Creates an event task with the given description and time range.
     *
     * @param description the text describing the event.
     * @param from the start date/time, in a format recognized by {@link FlorkDateTime},
     *             or free-form text if not recognized.
     * @param to the end date/time, in a format recognized by {@link FlorkDateTime},
     *           or free-form text if not recognized.
     */
    public Event(String description, String from, String to) {
        super(description);
        if (from == null || from.trim().isEmpty()) {
            throw new IllegalArgumentException("Event start time cannot be blank.");
        }
        if (to == null || to.trim().isEmpty()) {
            throw new IllegalArgumentException("Event end time cannot be blank.");
        }
        this.from = FlorkDateTime.parse(from.trim());
        this.to = FlorkDateTime.parse(to.trim());
        validateTimeRange();
    }

    private void validateTimeRange() {
        LocalDate fromDate = from.getDateOrNull();
        LocalDate toDate = to.getDateOrNull();
        if (fromDate != null && toDate != null && toDate.isBefore(fromDate)) {
            throw new IllegalArgumentException("Event end time must be after the start time.");
        }
        if (from.getDateTimeOrNull() != null && to.getDateTimeOrNull() != null
                && !to.getDateTimeOrNull().isAfter(from.getDateTimeOrNull())) {
            throw new IllegalArgumentException("Event end time must be later than the start time.");
        }
    }

    /**
     * Returns the event's start date/time as a display-friendly string.
     *
     * @return the formatted "from" value.
     */
    public String getFrom() {
        return from.toDisplayString();
    }

    /**
     * Returns the event's end date/time as a display-friendly string.
     *
     * @return the formatted "to" value.
     */
    public String getTo() {
        return to.toDisplayString();
    }

    /**
     * Returns whether this event occurs on the given date. If both the start
     * and end are recognized dates, the check is an inclusive range match;
     * if only one side is a recognized date, only that side is compared.
     *
     * @param queryDate the date to check against.
     * @return true if the event occurs on queryDate, false otherwise.
     */
    @Override
    public boolean isOccurringOn(LocalDate queryDate) {
        LocalDate fromDate = from.getDateOrNull();
        LocalDate toDate = to.getDateOrNull();
        if (fromDate != null && toDate != null) {
            return !queryDate.isBefore(fromDate) && !queryDate.isAfter(toDate);
        } else if (fromDate != null) {
            return fromDate.equals(queryDate);
        } else if (toDate != null) {
            return toDate.equals(queryDate);
        } else {
            return false;
        }
    }

    @Override
    public String toSaveFormat() {
        return "E | " + super.toSaveFormat() + " | " + from.toSaveFormat() + " | " + to.toSaveFormat();
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + this.getFrom() + " to: " + this.getTo() + ")";
    }
}
