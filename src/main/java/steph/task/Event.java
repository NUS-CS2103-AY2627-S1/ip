package steph.task;

import java.time.LocalDateTime;

import steph.DateTimes;

/**
 * A task that spans a range, from a start date-time to an end date-time.
 */
public class Event extends Task {

    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Constructs an event with the given name and start and end date-times.
     *
     * @param name Description of the task.
     * @param from Start date-time of the event.
     * @param to   End date-time of the event.
     */
    public Event(String name, LocalDateTime from, LocalDateTime to) {
        super(name);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns whether this event's span overlaps {@code other}'s. Ranges are
     * half-open ({@code [from, to)}), so two events that are merely
     * back-to-back -- one ending exactly when the other starts -- do not
     * clash. An end date-time that falls exactly on midnight is treated as
     * running through the end of that day rather than as a single instant
     * (see {@link DateTimes#expandIfMidnight}).
     *
     * @param other The event to compare against.
     * @return True if the two events' spans overlap.
     */
    public boolean clashesWith(Event other) {
        LocalDateTime thisEnd = DateTimes.expandIfMidnight(this.to);
        LocalDateTime otherEnd = DateTimes.expandIfMidnight(other.to);
        return this.from.isBefore(otherEnd) && other.from.isBefore(thisEnd);
    }

    /**
     * Returns the full save-file line for this event, e.g.
     * {@code "E | 0 | camp | 2019-10-15T00:00 | 2019-10-16T00:00"}.
     */
    @Override
    public String toFileFormat() {
        return "E" + super.toFileFormat()
                + " " + FIELD_SEPARATOR + " " + DateTimes.toStorageFormat(this.from)
                + " " + FIELD_SEPARATOR + " " + DateTimes.toStorageFormat(this.to);
    }

    /**
     * Returns this event as
     * {@code "[E][ ] camp (from: Oct 15 2019 to: Oct 16 2019)"}.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + DateTimes.toDisplayFormat(this.from)
                + " to: " + DateTimes.toDisplayFormat(this.to) + ")";
    }
}
