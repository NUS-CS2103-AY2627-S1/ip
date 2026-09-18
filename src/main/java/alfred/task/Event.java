package alfred.task;

import java.time.LocalDate;
import java.util.List;

import alfred.Dates;

/**
 * Represents a task that spans a stretch of days, for example
 * {@code [E][ ] project meeting (from: Dec 02 2019 to: Dec 03 2019)}.
 */
public class Event extends Task {
    private final LocalDate from;

    private final LocalDate to;

    /**
     * Creates an event that starts out not done.
     *
     * @param description what the event is.
     * @param from the day it starts.
     * @param to the day it ends.
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public List<String> toFileFields() {
        return buildFileFields("E", from.toString(), to.toString());
    }

    /**
     * {@inheritDoc}
     *
     * <p>Two events are the same task only if they start and end on the same
     * days.
     */
    @Override
    public boolean isSameTask(Task other) {
        // The class check has to come before the cast, or comparing with a
        // todo or a deadline would throw rather than merely differ.
        if (!super.isSameTask(other)) {
            return false;
        }
        Event otherEvent = (Event) other;
        return from.equals(otherEvent.from) && to.equals(otherEvent.to);
    }

    /**
     * {@inheritDoc}
     *
     * <p>An event falls on every day it spans, its first and its last included.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(from) && !date.isAfter(to);
    }

    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + Dates.format(from)
                + " to: " + Dates.format(to) + ")";
    }
}
