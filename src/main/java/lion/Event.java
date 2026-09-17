package lion;

import java.time.LocalDateTime;

/**
 * Represents a task that takes place between a specified start and end.
 */
public class Event extends Task {
    /** User-provided event start details. */
    protected String from;
    /** User-provided event end details. */
    protected String to;

    /**
     * Creates an event with its description, start, and end details.
     *
     * @param description description of the event.
     * @param from event start text.
     * @param to event end text.
     */
    public Event(String description, String from, String to) {
        super(description);
        // Both fields are always supplied by Parser.getEventParts() or
        // Storage.decode(); a null here would only fail later, in toString().
        assert from != null : "event start text must not be null";
        assert to != null : "event end text must not be null";

        this.from = from;
        this.to = to;
    }

    /**
     * Returns the one-letter icon used to identify event tasks.
     *
     * @return {@code E}.
     */
    @Override
    public String getTypeIcon() {
        return "E";
    }

    /**
     * Validates that, when both start and end are full date-times in the
     * {@code d/M/yyyy HHmm} format, the start is strictly before the end.
     *
     * <p>Plain free-text start/end values (e.g. "8pm") are not date-times and are left
     * unvalidated, since Event — unlike Deadline — has always accepted arbitrary text
     * for its start and end; only inputs that are actually comparable dates are checked.
     * Uses {@link Deadline#parseStrict(String)} so a date that does not exist
     * (e.g. 30 February) is likewise treated as non-comparable free text here, rather
     * than being silently clamped to a nearby valid date and compared anyway.
     *
     * @param from event start text.
     * @param to event end text.
     * @throws LionException if both parse as date-times and the start is not before the end.
     */
    public static void validateRange(String from, String to) throws LionException {
        LocalDateTime start = Deadline.parseStrict(from);
        LocalDateTime end = Deadline.parseStrict(to);

        if (start != null && end != null && !start.isBefore(end)) {
            throw new LionException("An event's start (" + from.trim() + ") must be before its end ("
                    + to.trim() + ") — Lion can't be in two places at once.");
        }
    }

    /**
     * Returns the event's start text for saving and restoring the event.
     *
     * @return event start text.
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the event's end text for saving and restoring the event.
     *
     * @return event end text.
     */
    public String getTo() {
        return to;
    }

    /**
     * Returns a display-friendly representation of this event.
     *
     * @return event status, description, start, and end.
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + this.from + " to: " + to + ")";
    }

    /**
     * Returns whether another task is an event with the same description, start, and end.
     *
     * @param other task to compare against.
     * @return true if the two events share the same description, start, and end.
     */
    @Override
    public boolean hasSameDetails(Task other) {
        if (!super.hasSameDetails(other)) {
            return false;
        }

        Event otherEvent = (Event) other;
        return from.equals(otherEvent.from) && to.equals(otherEvent.to);
    }
}
