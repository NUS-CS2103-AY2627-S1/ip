package fifi.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import fifi.Parser;

/**
 * Represents a task that happens from one date or time to another.
 */
public class Event extends Task {
    private final LocalDate start;
    private final LocalDate end;

    /**
     * Creates an event task with its completion status, description, start date, and end date.
     *
     * @param isMarked whether the event has been completed
     * @param description description of the event
     * @param start date when the event starts
     * @param end date when the event ends
     */
    public Event(boolean isMarked, String description, LocalDate start, LocalDate end) {
        super(isMarked, description);
        validateRange(start, end);
        this.start = start;
        this.end = end;
    }

    /**
     * Creates an event using timestamps loaded from storage.
     *
     * @param isMarked whether the event has been completed
     * @param description description of the event
     * @param start date when the event starts
     * @param end date when the event ends
     * @param createdAt exact time when the event was created, or null when unknown
     * @param lastMarkedAt exact time when the event was last completed, or null when never completed or unknown
     */
    public Event(boolean isMarked, String description, LocalDate start, LocalDate end,
            LocalDateTime createdAt, LocalDateTime lastMarkedAt) {
        super(isMarked, description, createdAt, lastMarkedAt);
        validateRange(start, end);
        this.start = start;
        this.end = end;
    }

    public LocalDate getStart() {
        return this.start;
    }

    public LocalDate getEnd() {
        return this.end;
    }

    @Override
    public Task copy() {
        return new Event(isMarked(), getDescription(), start, end,
                getCreatedAt().orElse(null), getLastMarkedAt().orElse(null));
    }

    private static void validateRange(LocalDate start, LocalDate end) {
        validateDate(start);
        validateDate(end);
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("Event end date cannot be earlier than start date.");
        }
    }

    @Override
    public boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other) && start.equals(((Event) other).start)
                && end.equals(((Event) other).end);
    }

    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(start) && !date.isAfter(end);
    }

    /**
     * Returns the text format used to save this event on disk.
     *
     * @return the saved representation of this event
     */
    @Override
    public String toFileString() {
        return String.format("E | %s | %s | %s | %s | %s", getMarkedStatus(), getDescription(),
                Parser.formatDateForStorage(getStart()), Parser.formatDateForStorage(getEnd()), getTimestampFields());
    }

    @Override
    public String toString() {
        return String.format("[E]" + super.toString() + " (from: %s to: %s)",
                Parser.formatDateForDisplay(getStart()), Parser.formatDateForDisplay(getEnd()));
    }
}
