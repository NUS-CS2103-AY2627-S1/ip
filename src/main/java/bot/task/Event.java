package bot.task;

import java.time.LocalDate;

import bot.parser.TaskDateTime;

/**
 * Represents a task that starts at a specific date/time and ends at a specific date/time.
 */
public class Event extends Task {

    /** The date (and optionally time) this event starts. */
    protected TaskDateTime from;
    /** The date (and optionally time) this event ends. */
    protected TaskDateTime to;

    /**
     * Creates a new, not-done event with the given description and start/end date/time.
     *
     * @param description The event's description text.
     * @param from The date (and optionally time) the event starts.
     * @param to The date (and optionally time) the event ends.
     */
    public Event(String description, TaskDateTime from, TaskDateTime to) {
        super(description);
        // from/to always come from successful TaskDateTime.parse() calls
        // (in Parser.parseEvent or Storage.parseLine), which either return
        // a non-null value or throw - so neither should ever be null
        // here; toString/occursOn/toSaveFormat all dereference both.
        assert from != null && to != null : "an Event's from/to date-times must not be null";
        this.from = from;
        this.to = to;
    }

    /**
     * Returns this event's status icon, description, and start/end date/time,
     * prefixed with the {@code [E]} type icon.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }

    /**
     * Returns this event's data-file line, e.g.
     * {@code "E | 1 | project meeting | 2019-10-15 1400 | 2019-10-15 1600"}.
     */
    @Override
    public String toSaveFormat() {
        return "E | " + (isDone() ? "1" : "0") + " | " + getDescription() + " | " + from.toSaveFormat() + " | "
                + to.toSaveFormat();
    }

    /**
     * Returns whether the given date falls within this event's start and end
     * dates, inclusive.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(from.toLocalDate()) && !date.isAfter(to.toLocalDate());
    }

    /** Returns this event's start date, used to order it for the {@code sort} command. */
    @Override
    protected LocalDate getSortDate() {
        return from.toLocalDate();
    }
}
