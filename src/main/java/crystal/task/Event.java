package crystal.task;

import java.time.LocalDate;

/**
 * Represents a task that takes place over a period of time.
 */
public class Event extends Task {
    /** Event start date, time, or unrestricted scheduling text. */
    protected TaskDateTime from;

    /** Event end date, time, or unrestricted scheduling text. */
    protected TaskDateTime to;

    /**
     * Creates an incomplete event.
     *
     * @param description description of the event.
     * @param from event start date, time, or unrestricted text.
     * @param to event end date, time, or unrestricted text.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = TaskDateTime.parse(from);
        this.to = TaskDateTime.parse(to);
    }

    /**
     * Returns this event in the format stored on disk.
     *
     * @return pipe-separated event data.
     */
    @Override
    public String toDataString() {
        return "E | " + super.toDataString() + " | " + from + " | " + to;
    }

    /**
     * Returns whether this event occurs on the specified date. A dated event
     * with both a start and end date covers the inclusive range between them.
     *
     * @param date date to check.
     * @return {@code true} if the event occurs on the date.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        LocalDate fromDate = from.getDate();
        LocalDate toDate = to.getDate();
        if (fromDate != null && toDate != null && !toDate.isBefore(fromDate)) {
            return !date.isBefore(fromDate) && !date.isAfter(toDate);
        }
        return from.occursOn(date) || to.occursOn(date);
    }

    /**
     * Returns this task with its event type icon and time period.
     *
     * @return formatted event details.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
