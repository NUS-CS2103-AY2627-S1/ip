package lebronjames.task;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a task that occurs between specified start and end times.
 */
public class Event extends Task {
    /** Letter used to identify an event in the save file. */
    public static final String FILE_TYPE = "E";

    private final TaskDateTime from;
    private final TaskDateTime to;

    /**
     * Creates an incomplete event task.
     *
     * @param description Description of the event.
     * @param from Start date, and optionally time, of the event.
     * @param to End date, and optionally time, of the event.
     */
    public Event(String description, TaskDateTime from, TaskDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * {@inheritDoc}
     *
     * <p>An event counts as occurring on a date if that date falls anywhere
     * between its start and end dates, so a multi-day event is listed on every
     * day it spans, not only on the day it begins.
     */
    @Override
    public boolean isOn(LocalDate date) {
        return !date.isBefore(from.getDate()) && !date.isAfter(to.getDate());
    }

    @Override
    public String toFileFormat() {
        return FILE_TYPE + " | " + getDoneFlag() + " | " + getDescription()
                + " | " + from.toFileFormat() + " | " + to.toFileFormat();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Both ends are compared, so the same meeting held on two different days
     * counts as two tasks rather than as a duplicate.
     */
    @Override
    public boolean equals(Object other) {
        if (!super.equals(other)) {
            return false;
        }

        // super.equals has already established that other is an Event.
        Event otherEvent = (Event) other;
        return from.equals(otherEvent.from) && to.equals(otherEvent.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), from, to);
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
