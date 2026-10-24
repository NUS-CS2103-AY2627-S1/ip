package echo.task;

import java.time.LocalDate;

/**
 * A task that spans an inclusive date range from "from" to "to".
 */
public class Event extends Task {
    protected LocalDate from;
    protected LocalDate to;

    /**
     * Creates an Event task spanning the given inclusive date range.
     *
     * @param description What the event is.
     * @param from The first date the event occurs on.
     * @param to The last date the event occurs on.
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description, TaskType.EVENT);
        // Parser.parseEvent rejects a "/to" date before "/from" before ever
        // constructing an Event, so every event's range should already be
        // ordered. occursOn() relies on from <= to to work correctly, so
        // this documents and checks that assumption at the source.
        assert !to.isBefore(from) : "event's 'to' date should not be before its 'from' date";
        this.from = from;
        this.to = to;
    }

    /**
     * Returns this task's display line plus its date range, e.g.
     * "[E][ ] trip (from: Aug 01 2019 to: Aug 03 2019)".
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + from.format(DISPLAY_DATE_FORMAT)
                + " to: " + to.format(DISPLAY_DATE_FORMAT) + ")";
    }

    /**
     * Returns this task's save-file line plus its date range in ISO
     * format, e.g. "E | 0 | trip | 2019-08-01 | 2019-08-03".
     */
    @Override
    public String toFileFormat() {
        return super.toFileFormat() + FILE_FORMAT_SEPARATOR + from + FILE_FORMAT_SEPARATOR + to;
    }

    /**
     * Returns true if the given date falls within this event's
     * inclusive date range.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(from) && !date.isAfter(to);
    }

    /**
     * Returns whether the given task is a duplicate of this one: same
     * description (per the superclass check) and the same date range.
     */
    @Override
    public boolean isDuplicateOf(Task other) {
        return super.isDuplicateOf(other)
                && this.from.equals(((Event) other).from)
                && this.to.equals(((Event) other).to);
    }
}
