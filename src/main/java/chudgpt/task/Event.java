package chudgpt.task;

import java.time.LocalDate;

/** A task that takes place during a specified time range. */
public class Event extends Task {
    /** Start date of this event. */
    protected final LocalDate start;

    /** End date of this event. */
    protected final LocalDate end;

    /**
     * Creates an event task with the specified description and date range.
     *
     * @param description task description.
     * @param start event start date.
     * @param end event end date.
     */
    public Event(String description, LocalDate start, LocalDate end) {
        super(description);
        assert start != null : "Event start date should not be null";
        assert end != null : "Event end date should not be null";
        assert start.isBefore(end) : "Event start date should be before its end date";
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other)
                && start.equals(((Event) other).start)
                && end.equals(((Event) other).end);
    }

    @Override
    public String toString() {
        return String.format("[E]%s (from: %s to: %s)", super.toString(), start, end);
    }

    /**
     * Converts a task to its representation in the save file.
     *
     * @return representation of the task in the save file.
     */
    @Override
    public String toSaveMessage() {
        return String.format("E | %d | %s | %s | %s | %s", isCompleted ? 1 : 0,
                getPriority(), description, start, end);
    }
}
