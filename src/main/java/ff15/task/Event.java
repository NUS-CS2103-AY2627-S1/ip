package ff15.task;

/**
 * Represents a task that starts on a specific date and ends on a specific date,
 * either of which may also carry a time of day. Both are held as
 * {@link TaskTime}s so that the event can be compared against a {@link DateRange}.
 */
public class Event extends Task {
    /** When the event starts. */
    protected TaskTime from;

    /** When the event ends. */
    protected TaskTime to;

    /**
     * Creates an event.
     *
     * @param description what the user typed to describe the task.
     * @param from the date, and optionally the time, the event starts.
     * @param to the date, and optionally the time, the event ends.
     */
    public Event(String description, TaskTime from, TaskTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /** Two events are the same when they match as tasks and run between the same times. */
    @Override
    public boolean isSameAs(Task other) {
        if (!super.isSameAs(other)) { // settles whether other is an Event at all, before the cast
            return false;
        }
        Event that = (Event) other;
        return from.equals(that.from) && to.equals(that.to);
    }

    /**
     * An event falls in a span when it is running for at least one day of it —
     * an event lasting a whole week shows up in a query for any day of that week,
     * not only its first or last day.
     */
    @Override
    public boolean occursIn(DateRange range) {
        return range.overlaps(from.getDate(), to.getDate());
    }

    /**
     * Returns this task with both ends, e.g.
     * {@code [E][ ] holiday (from: Dec 20 2019 to: Dec 26 2019)}.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + from + " to: " + to + ")";
    }

    /**
     * Returns this task in save-file form, e.g.
     * {@code E | 0 | holiday | 2019-12-20 | 2019-12-26}.
     */
    @Override
    public String toFileFormat() {
        return "E | " + super.toFileFormat()
                + " | " + from.toFileFormat() + " | " + to.toFileFormat();
    }
}
