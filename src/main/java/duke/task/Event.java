package duke.task;

import java.time.LocalDate;

/**
 * Represents an event task with a description, start date, and end date.
 */
public class Event extends Task {
    /** Start date of the event. */
    private final LocalDate start;
    /** End date of the event. */
    private final LocalDate end;

    /**
     * Creates an event task.
     *
     * @param description Description of the event.
     * @param start Start date of the event.
     * @param end End date of the event.
     */
    public Event(String description, LocalDate start, LocalDate end) {
        super(description);
        this.start = start;
        this.end = end;
    }

    /**
     * Checks whether another event has the same description, start date, and end date.
     *
     * @param other Task to compare with.
     * @return {@code true} if the event details match.
     */
    @Override
    public boolean hasSameDetails(Task other) {
        return other instanceof Event otherEvent
                && super.hasSameDetails(other)
                && start.equals(otherEvent.start)
                && end.equals(otherEvent.end);
    }

    /**
     * Returns a user-facing representation of an event task.
     *
     * @return A display string prefixed with {@code [E]} and including start and end dates.
     */
    @Override
    public String toString() {
        return "[E] " + super.toString() + " (from: " + this.start + " to: " + this.end + ")";
    }

    /**
     * Returns the storage format for an event task.
     *
     * @return A single-line data string containing type, done flag, description, start date, and end date.
     */
    @Override
    public String toDataString() {
        return "E | " + (isDone ? "1" : "0") + " | " + description + " | " + start + " | " + end;
    }
}
