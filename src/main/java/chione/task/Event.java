package chione.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import chione.DateTimes;

/**
 * A task that starts and ends at a given date or time,
 * e.g. {@code "project meeting /from 2019-10-15 1400 /to 2019-10-15 1600"}.
 *
 * <p>Displayed as
 * {@code [E][ ] project meeting (from: Oct 15 2019, 2:00pm to: Oct 15 2019, 4:00pm)}.
 */
public class Event extends Task {
    /** The letter that marks a saved line as an event. */
    public static final String TYPE_LETTER = "E";

    /** When the event starts, held as a real moment like {@link Deadline#by}. */
    protected LocalDateTime from;

    /** When the event ends, held as a real moment like {@link Deadline#by}. */
    protected LocalDateTime to;

    /**
     * Creates an event that starts out not done.
     *
     * @param description what the event is
     * @param from        when it starts
     * @param to          when it ends
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        // Only that both moments exist: a start later than the end is something
        // the user can type, so it is not a broken assumption.
        assert from != null && to != null : "an event needs both of its moments";
        this.from = from;
        this.to = to;
    }

    /**
     * Reports whether this event is running on the given day.
     *
     * <p>An event can span several days, so the day counts as a match when it
     * falls anywhere between the start and the end, both included. That is
     * written as "not before the start and not after the end" because
     * {@link LocalDate} offers no single "is between" test.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(from.toLocalDate()) && !date.isAfter(to.toLocalDate());
    }

    /**
     * Reports whether this event repeats another task: same description, and
     * running between the same two moments.
     *
     * <p>The class check in {@code super} guarantees the cast below is safe.
     */
    @Override
    public boolean isDuplicateOf(Task other) {
        if (!super.isDuplicateOf(other)) {
            return false;
        }
        Event that = (Event) other;
        return from.equals(that.from) && to.equals(that.to);
    }

    /** Returns the task prefixed with its type icon and followed by its time range. */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + DateTimes.format(from) + " to: " + DateTimes.format(to) + ")";
    }

    /**
     * Returns the saved line with the type letter in front and both moments
     * appended, e.g. {@code "E | 0 | meeting | 2019-10-15 1400 | 2019-10-15 1600"}.
     */
    @Override
    public String toSaveFormat() {
        return TYPE_LETTER + SEPARATOR + super.toSaveFormat()
                + SEPARATOR + DateTimes.toSaveFormat(from)
                + SEPARATOR + DateTimes.toSaveFormat(to);
    }
}
