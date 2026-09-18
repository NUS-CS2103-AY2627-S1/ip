package davidgoggins.task;

import java.time.LocalDate;

import davidgoggins.DavidGogginsException;

/**
 * A task that runs from a start date to an end date.
 *
 * <p>Both dates are required and the end may not come before the start, so an event
 * that exists at all covers a sensible range.
 */
public class Event extends Task {
    /** A correct event command, suggested when a date cannot be read. */
    private static final String EXAMPLE = "event project meeting /from 2026-09-10 /to 2026-09-11";

    /** When the event starts. */
    private final LocalDate from;

    /** When the event ends, never earlier than {@link #from}. */
    private final LocalDate to;

    /**
     * Creates an event that is not done yet.
     *
     * @param description the task's text
     * @param from        when it starts, as {@code yyyy-mm-dd}, e.g. {@code 2019-10-15}
     * @param to          when it ends, in the same format, and not before {@code from}
     * @throws DavidGogginsException if either date is malformed, or the end is before the start
     */
    public Event(String description, String from, String to) throws DavidGogginsException {
        super(description);
        this.from = parseDate(from, EXAMPLE);
        this.to = parseDate(to, EXAMPLE);

        if (this.to.isBefore(this.from)) {
            throw new DavidGogginsException("An event cannot end before it starts: you gave"
                    + " a start of \"" + from + "\" and an end of \"" + to + "\".");
        }
    }

    /** Shown as {@code [E][ ] project meeting (from: 2019-10-15 to: 2019-10-16)}. */
    @Override
    public String toString() {
        return "[E]" + getStatusIcon() + description + " (from: " + from + " to: " + to + ")";
    }

    /** Saved as {@code E | 0 | project meeting | 2019-10-15 | 2019-10-16}. */
    @Override
    public String toSaveFormat() {
        // The constructor refuses a backwards range and the fields are final, so this
        // still holds; saving a backwards event would make the file unloadable.
        assert !to.isBefore(from) : "event ends before it starts";
        return toSaveFormat("E", from.toString(), to.toString());
    }

    /**
     * {@inheritDoc}
     *
     * <p>An event is only a duplicate if both of its dates also match.
     */
    @Override
    public boolean isDuplicateOf(Task other) {
        // super checks the class first, so the cast below is always safe.
        if (!super.isDuplicateOf(other)) {
            return false;
        }
        Event otherEvent = (Event) other;
        return from.equals(otherEvent.from) && to.equals(otherEvent.to);
    }
}
