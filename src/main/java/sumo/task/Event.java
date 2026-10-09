package sumo.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * A task that takes place between a specified start and end date or time.
 */
public class Event extends Task {
    private final LocalDateTime from;
    private final LocalDateTime to;
    private final boolean hasFromTime;
    private final boolean hasToTime;

    /**
     * Creates a new incomplete event.
     *
     * @param description the event text.
     * @param from the event start date and time.
     * @param to the event end date and time.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        this(description, from, to, true, true);
    }

    /**
     * Creates a new incomplete event for date-only values.
     *
     * @param description the event text.
     * @param from the event start date.
     * @param to the event end date.
     */
    public Event(String description, LocalDate from, LocalDate to) {
        this(description, from.atStartOfDay(), to.atStartOfDay(), false, false);
    }

    /**
     * Creates an event while retaining whether each input included a time.
     *
     * @param description the event text.
     * @param from the event start date and time.
     * @param to the event end date and time.
     * @param hasFromTime whether the start input included a time.
     * @param hasToTime whether the end input included a time.
     * @throws IllegalArgumentException if the end is not after the start.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to,
            boolean hasFromTime, boolean hasToTime) {
        super(description);
        assert from != null && to != null : "An event must have both endpoints.";
        if (!from.isBefore(to)) {
            throw new IllegalArgumentException("An event's end must be after its start.");
        }
        assert hasFromTime || from.toLocalTime().equals(LocalTime.MIDNIGHT)
                : "A date-only event start must be normalized to midnight.";
        assert hasToTime || to.toLocalTime().equals(LocalTime.MIDNIGHT)
                : "A date-only event end must be normalized to midnight.";
        this.from = from;
        this.to = to;
        this.hasFromTime = hasFromTime;
        this.hasToTime = hasToTime;
    }

    /**
     * Returns the event type icon.
     *
     * @return the event type icon.
     */
    @Override
    public String getTypeIcon() {
        return TaskType.EVENT.getStorageCode();
    }

    /**
     * Returns the event start date or time.
     *
     * @return the event start date or time.
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns the event end date or time.
     *
     * @return the event end date or time.
     */
    public LocalDateTime getTo() {
        return to;
    }

    /** Returns whether another event has the same description and both endpoints. */
    @Override
    public boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other) && from.equals(((Event) other).from)
                && to.equals(((Event) other).to);
    }

    /**
     * Returns this event in the line-based format used for persistent storage.
     *
     * @return the task type, completion status, description, start, and end.
     */
    @Override
    public String toDataString() {
        String storedFrom = hasFromTime ? from.toString() : from.toLocalDate().toString();
        String storedTo = hasToTime ? to.toString() : to.toLocalDate().toString();
        return super.toDataString() + " | " + storedFrom + " | " + storedTo;
    }

    /**
     * Returns this event in the format used by Sumo's task list.
     *
     * @return the formatted event.
     */
    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] "
                + getDescription() + " (from: " + DateTimeDisplay.format(from, hasFromTime)
                + " to: " + DateTimeDisplay.format(to, hasToTime) + ")";
    }
}
