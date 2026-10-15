package kiwi.task;

import java.time.LocalDate;

import kiwi.KiwiException;

/**
 * An event task with a start and end calendar date (inclusive range).
 */
public class Event extends Task {
    private final LocalDate from;
    private final LocalDate to;

    /**
     * Creates an event task.
     *
     * @param description what the event is.
     * @param from start date (inclusive).
     * @param to end date (inclusive).
     * @throws KiwiException If {@code to} is before {@code from}.
     */
    public Event(String description, LocalDate from, LocalDate to) throws KiwiException {
        super(description, TaskType.EVENT);
        assert from != null : "Event start date should already be parsed";
        assert to != null : "Event end date should already be parsed";
        if (to.isBefore(from)) {
            throw new KiwiException("Event end date cannot be before the start date.");
        }
        assert !to.isBefore(from) : "Validated event range is from <= to";
        this.from = from;
        this.to = to;
    }

    /**
     * {@inheritDoc}
     * Also requires the same start and end dates.
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
     * {@inheritDoc}
     *
     * @return {@code true} if {@code date} falls in this event's inclusive from–to range.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        boolean isOnOrAfterStart = !date.isBefore(from);
        boolean isOnOrBeforeEnd = !date.isAfter(to);
        return isOnOrAfterStart && isOnOrBeforeEnd;
    }

    /**
     * {@inheritDoc}
     * Appends the start and end dates in {@code yyyy-MM-dd} form so they can be loaded again.
     */
    @Override
    public String toSaveFormat() {
        return super.toSaveFormat() + SAVE_FIELD_SEPARATOR + from.format(KiwiDate.INPUT_FORMAT)
                + SAVE_FIELD_SEPARATOR + to.format(KiwiDate.INPUT_FORMAT);
    }

    /**
     * {@inheritDoc}
     * Appends {@code (from: MMM dd yyyy to: MMM dd yyyy)} for display.
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + KiwiDate.format(from)
                + " to: " + KiwiDate.format(to) + ")";
    }
}
