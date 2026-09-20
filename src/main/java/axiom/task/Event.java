package axiom.task;

import java.time.LocalDateTime;
import java.util.Optional;

import axiom.parser.DateTimeParser;

/**
 * Represents an event task with a start and end date or time.
 */
public class Event extends Task {
    private static final String TYPE_PREFIX = "[E]";

    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Creates a new event task.
     *
     * @param description Description of the event.
     * @param from Start date and time of the event.
     * @param to End date and time of the event.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        // DateTimeParser always returns parsed values; null times mean a caller bug.
        assert from != null : "Event /from date-time should not be null";
        assert to != null : "Event /to date-time should not be null";
        assert from.isBefore(to) : "Event /from must be earlier than /to";
        this.from = from;
        this.to = to;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasSameDetails(Task other) {
        if (!super.hasSameDetails(other)) {
            return false;
        }
        Event event = (Event) other;
        return from.equals(event.from) && to.equals(event.to);
    }

    /**
     * Returns the event start date and time.
     *
     * @return The {@code /from} date and time.
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns the event end date and time.
     *
     * @return The {@code /to} date and time.
     */
    public LocalDateTime getTo() {
        return to;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<LocalDateTime> getChronologicalDate() {
        return Optional.of(from);
    }

    /**
     * Returns a string representation with the {@code [E]} type prefix and formatted times.
     *
     * @return Formatted event string.
     */
    @Override
    public String toString() {
        return TYPE_PREFIX + super.toString() + " (from: " + DateTimeParser.format(from)
                + " to: " + DateTimeParser.format(to) + ")";
    }
}
