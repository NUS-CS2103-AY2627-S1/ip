package meowmeow.task;

import java.time.LocalDate;

/**
 * A task spanning a start and end point, added via the "event" command.
 * "from" and "to" are real {@link TaskDateTime} values, so Meowmeow parses
 * them on input, re-formats them for display, and can tell whether a given
 * date falls within the event's span (used by "list &lt;date&gt;"). Renders
 * with an "[E]" tag (via {@link TaskType#EVENT}) and both endpoints in
 * parentheses.
 */
public class Event extends Task {

    protected TaskDateTime from;
    protected TaskDateTime to;

    /**
     * Constructs an event with the given description and start/end points.
     *
     * @param description the task text as the user typed it.
     * @param from        the date (and optional time) the event starts.
     * @param to          the date (and optional time) the event ends.
     */
    public Event(String description, TaskDateTime from, TaskDateTime to) {
        super(description, TaskType.EVENT);
        // Both callers (Parser and Storage) parse the "/from" and "/to" values
        // into real TaskDateTime objects before constructing the event.
        assert from != null : "an event must have a start date";
        assert to != null : "an event must have an end date";
        this.from = from;
        this.to = to;
    }

    /** Returns {@code true} when {@code date} is on the start day, the end day, or in between. */
    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(from.getDate()) && !date.isAfter(to.getDate());
    }

    @Override
    protected String details() {
        return " (from: " + from + " to: " + to + ")";
    }

    @Override
    protected String fileDetails() {
        return " | " + from.toFileString() + " | " + to.toFileString();
    }
}
