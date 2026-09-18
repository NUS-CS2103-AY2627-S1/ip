package chud.task;

import java.time.LocalDate;

/** A task with a description and a start ("from") and end ("to") date/time. */
public class Event extends Task {
    protected TaskDateTime from;
    protected TaskDateTime to;

    /** Creates a new, not-yet-done event with the given description, start, and end date/time. */
    public Event(String description, TaskDateTime from, TaskDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EVENT;
    }

    /**
     * Matches any date from (inclusive) to (inclusive), not just the exact start or end date.
     *
     * @param date The date to check.
     * @return True if date falls on or between this event's start and end date.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(from.getDate()) && !date.isAfter(to.getDate());
    }

    @Override
    public LocalDate getSortDate() {
        return from.getDate();
    }

    @Override
    public String toString() {
        return super.toString() + " (from: " + from + " to: " + to + ")";
    }

    @Override
    public String toFileString() {
        return super.toFileString() + " | " + from.toStorageString() + " | " + to.toStorageString();
    }
}
