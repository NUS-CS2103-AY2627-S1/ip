package shannon.task;

import shannon.exception.InvalidEventTimesException;

/**
 * A task that starts at one date/time and ends at a later one,
 * e.g. {@code team meeting from 2026-08-09 14:00 to 2026-08-09 16:00}.
 * <p>
 * The start and end are kept as {@link EventTime}s rather than as the text the user typed, so
 * that an event can be refused when it would end before it starts, the same way a
 * {@link Deadline} refuses a date that does not exist.
 */
public class Event extends Task {

    /** The letter that marks an event in the save file. */
    public static final String TYPE_CODE = "E";

    /** When the event starts. */
    private final EventTime from;

    /** When the event ends; always after {@link #from}. */
    private final EventTime to;

    /**
     * Creates an event that is not yet done.
     *
     * @param description what the event is.
     * @param from        when it starts.
     * @param to          when it ends.
     * @throws InvalidEventTimesException if it does not end after it starts.
     */
    public Event(String description, EventTime from, EventTime to) throws InvalidEventTimesException {
        super(description);
        assert from != null && to != null : "Event start and end must not be null";
        // Checked here rather than in Parser, so that a hand-edited save file is held to the same
        // rule as the user's typing: no Event object can exist that ends before it starts.
        if (!from.toStartDateTime().isBefore(to.toEndDateTime())) {
            throw new InvalidEventTimesException(from.toString(), to.toString());
        }
        this.from = from;
        this.to = to;
    }

    /** Renders as {@code [E][X] team meeting (from: Aug 09 2026 14:00 to: Aug 09 2026 16:00)}. */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }

    /** Renders as {@code E | 0 | team meeting | 2026-08-09 14:00 | 2026-08-09 16:00}. */
    @Override
    public String toFileFormat() {
        return encode(TYPE_CODE) + " | " + escape(from.toFileFormat()) + " | " + escape(to.toFileFormat());
    }

    /** {@inheritDoc} For an event, the start and end must match too. */
    @Override
    public boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other) && other instanceof Event event
                && from.equals(event.from) && to.equals(event.to);
    }
}
