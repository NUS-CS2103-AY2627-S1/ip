package zaru.task;

import java.time.LocalDateTime;

import zaru.exception.ZaruException;
import zaru.parser.DateTimeParser;

/** Represents a task that occurs between a specified start and end time. */
public class Event extends Task {
    private static final int TYPE_PRIORITY = 2;

    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Creates an incomplete event from user-entered time text.
     *
     * @param title Task description.
     * @param from Event start time text.
     * @param to Event end time text.
     * @throws ZaruException If either time text is invalid.
     */
    public Event(String title, String from, String to) throws ZaruException {
        this(title, false, from, to);
    }

    /**
     * Creates an event from user-entered time text.
     *
     * @param title Task description.
     * @param isCompleted Whether the task is already complete.
     * @param from Event start time text.
     * @param to Event end time text.
     * @throws ZaruException If either time text is invalid.
     */
    public Event(String title, boolean isCompleted, String from, String to) throws ZaruException {
        this(title, isCompleted, DateTimeParser.parse(from), DateTimeParser.parse(to));
    }

    /**
     * Creates an incomplete event from parsed times.
     *
     * @param title Task description.
     * @param from Event start time.
     * @param to Event end time.
     */
    public Event(String title, LocalDateTime from, LocalDateTime to) throws ZaruException {
        this(title, false, from, to);
    }

    /**
     * Creates an event from parsed times.
     *
     * @param title Task description.
     * @param isCompleted Whether the task is already complete.
     * @param from Event start time.
     * @param to Event end time.
     * @throws ZaruException If the event does not end after it starts.
     */
    public Event(String title, boolean isCompleted, LocalDateTime from, LocalDateTime to) throws ZaruException {
        super(title, isCompleted);
        assert from != null : "Event start time should have been parsed before construction.";
        assert to != null : "Event end time should have been parsed before construction.";

        if (!from.isBefore(to)) {
            throw new ZaruException("Event start time must be earlier than its end time.");
        }

        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event start time.
     *
     * @return Event start time.
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns the event end time.
     *
     * @return Event end time.
     */
    public LocalDateTime getTo() {
        return to;
    }

    /**
     * Checks whether another event has the same description, start time, and end time.
     *
     * @param other Task to compare against.
     * @return {@code true} if both events have the same identifying details.
     */
    @Override
    public boolean hasSameDetails(Task other) {
        if (!super.hasSameDetails(other)) {
            return false;
        }

        Event otherEvent = (Event) other;
        return from.equals(otherEvent.from) && to.equals(otherEvent.to);
    }

    @Override
    protected int getTypePriority() {
        return TYPE_PRIORITY;
    }

    /**
     * Compares events by start time, then end time, then description.
     *
     * @param other Other event.
     * @return A negative value, zero, or a positive value according to the ordering.
     */
    @Override
    protected int compareWithinType(Task other) {
        Event otherEvent = (Event) other;
        int startComparison = from.compareTo(otherEvent.from);
        if (startComparison != 0) {
            return startComparison;
        }

        int endComparison = to.compareTo(otherEvent.to);
        if (endComparison != 0) {
            return endComparison;
        }

        return super.compareWithinType(other);
    }

    /**
     * Returns the task text with its event times displayed.
     *
     * @return Formatted event task text.
     */
    @Override
    public String toString() {
        return "[E]%s (from: %s) (to: %s)".formatted(
                super.toString(), DateTimeParser.format(from), DateTimeParser.format(to));
    }
}
