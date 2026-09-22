package yappa.task;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import yappa.exception.YappaException;
import yappa.util.DateUtil;

/**
 * Represents a task that occurs within a specific time range.
 */
public class Event extends Task {
    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Creates a new event, verifying the end is after its start.
     *
     * @param description Task description.
     * @param from        Event start date and time.
     * @param to          Event end date and time.
     * @throws YappaException If the event does not end after it starts.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) throws YappaException {
        super(description);
        validateDateTimes(from, to);
        // Validation guarantees that both values are safe to store.
        assert from != null && to != null && to.isAfter(from);
        this.from = from;
        this.to = to;
    }

    /**
     * Creates an event with the given completion state.
     *
     * @param description Task description.
     * @param isDone      Whether the task is completed.
     * @param from        Event start date and time.
     * @param to          Event end date and time.
     * @throws YappaException If the event does not end after it starts.
     */
    public Event(String description, boolean isDone, LocalDateTime from, LocalDateTime to)
            throws YappaException {
        super(description, isDone);
        validateDateTimes(from, to);
        // Validation guarantees that both values are safe to store.
        assert from != null && to != null && to.isAfter(from);
        this.from = from;
        this.to = to;
    }

    /**
     * Ensures that an event has a positive duration.
     *
     * @param from Event start date and time.
     * @param to   Event end date and time.
     * @throws YappaException If {@code to} is not after {@code from}.
     */
    private static void validateDateTimes(LocalDateTime from, LocalDateTime to) throws YappaException {
        if (from == null || to == null) {
            throw new YappaException("Event time must not be empty.");
        }
        if (!to.isAfter(from)) {
            throw new YappaException("Event end time must be after its start time :(.");
        }
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    @Override
    public Optional<LocalDateTime> getDateTime() {
        return Optional.of(getFrom());
    }

    /**
     * Formats this event for file storage.
     *
     * @return Storage representation of this event.
     */
    @Override
    public String toFileString() {
        return TaskType.EVENT.getCode()
                + " | " + (isDone() ? "1" : "0")
                + " | " + getDescription()
                + " | " + DateUtil.toFileString(from)
                + " | " + DateUtil.toFileString(to);
    }

    /**
     * Formats this event for display.
     *
     * @return Display representation of this event.
     */
    @Override
    public String toString() {
        return "[" + TaskType.EVENT.getCode() + "] "
                + super.toString()
                + " (from: " + DateUtil.toDisplayString(this.from)
                + " to: " + DateUtil.toDisplayString(this.to)
                + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Event other)) {
            return false;
        }
        return getDescription().equalsIgnoreCase(other.getDescription())
                && getTo().equals(other.getTo()) && getFrom().equals(other.getFrom());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getDescription().toLowerCase(),
                getFrom(),
                getTo());
    }

}
