package wobble.tasks;

import java.time.LocalDateTime;

import wobble.parser.DateTimeParser;

/** A task that takes place between a stated start and end time. */
public class Event extends Task {
    private final LocalDateTime from;
    private final LocalDateTime to;

    /** Creates an unfinished event with its start and end date/time. */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description, TaskType.EVENT);
        if (from == null || to == null) {
            throw new IllegalArgumentException("An event must have start and end dates");
        }
        if (!from.isBefore(to)) {
            throw new IllegalArgumentException("An event must end after it starts");
        }
        assert from != null : "An event must have a start date";
        assert to != null : "An event must have an end date";
        assert from.isBefore(to) : "An event must end after it starts";
        this.from = from;
        this.to = to;
    }

    /** Returns the event start text. */
    public LocalDateTime getFrom() {
        return from;
    }

    /** Returns the event end text. */
    public LocalDateTime getTo() {
        return to;
    }

    /** Returns whether this unfinished event has already ended. */
    public boolean isPast() {
        return !isDone() && to.isBefore(LocalDateTime.now());
    }

    /** Returns whether another event has the same description and date range. */
    @Override
    public boolean hasSameDetailsAs(Task other) {
        return other instanceof Event && super.hasSameDetailsAs(other)
                && from.equals(((Event) other).from)
                && to.equals(((Event) other).to);
    }

    /** Returns this event's display representation. */
    @Override
    public String toString() {
        String pastTag = isPast() ? "[PAST] " : "";
        return getDisplayPrefix() + pastTag + getDescription() + " (from: " + DateTimeParser.format(from)
                + " to: " + DateTimeParser.format(to) + ")";
    }
}
