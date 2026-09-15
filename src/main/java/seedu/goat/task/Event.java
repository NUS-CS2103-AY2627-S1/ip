package seedu.goat.task;

import java.time.LocalDate;

import seedu.goat.DateFormats;

/**
 * A task that spans a period of time, such as
 * {@code event project meeting /from 2019-10-15 /to 2019-10-16}.
 */
public class Event extends Task {

    /** The date the event starts. */
    private final LocalDate from;

    /** The date the event ends. */
    private final LocalDate to;

    /**
     * Creates an event that starts out not done.
     *
     * @param description what the event is.
     * @param from the date it starts.
     * @param to the date it ends.
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns whether this event is running on a given date.
     *
     * @param date the date to test.
     * @return true if the date falls within the event, endpoints included.
     */
    @Override
    public boolean isOn(LocalDate date) {
        return !date.isBefore(from) && !date.isAfter(to);
    }

    @Override
    public boolean isDuplicateOf(Task other) {
        if (!super.isDuplicateOf(other)) {
            return false;
        }

        // Only now is the cast safe: the base check has established that both tasks
        // are events, which casting before that check would have assumed too early.
        Event otherEvent = (Event) other;
        return from.equals(otherEvent.from) && to.equals(otherEvent.to);
    }

    @Override
    public String toFileString() {
        return "E | " + getFileStatus() + " | " + description + " | " + from + " | " + to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + DateFormats.format(from)
                + " to: " + DateFormats.format(to) + ")";
    }
}
