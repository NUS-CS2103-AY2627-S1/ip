package will.task;

import java.time.LocalDate;

/** A task that happens over a time span, e.g. "project meeting from Mon 2pm to 4pm". */
public class Event extends Task {
    protected FlexibleDate from;
    protected FlexibleDate to;

    /**
     * Creates an event task spanning the given start and end times.
     *
     * @param description What's happening.
     * @param from When it starts. Recognized as a date if it matches
     *             yyyy-MM-dd (see {@link FlexibleDate}), otherwise kept
     *             as free text.
     * @param to When it ends, parsed the same way as {@code from}.
     */
    public Event(String description, String from, String to) {
        super(description, TaskType.EVENT);
        this.from = new FlexibleDate(from);
        this.to = new FlexibleDate(to);
    }

    /** @return This event's display line, with its from/to times appended. */
    @Override
    public String toString() {
        return super.toString() + " (from: " + from.toDisplayString() + " to: " + to.toDisplayString() + ")";
    }

    /** @return This event's save-file line, with its from/to times appended. */
    @Override
    public String toSaveFormat() {
        return super.toSaveFormat() + " | " + from.toSaveString() + " | " + to.toSaveString();
    }

    /**
     * Matches if the queried date falls within [from, to] when both are
     * recognized dates, or equals whichever single end is a recognized
     * date if only one is (e.g. an event whose /to is free text like
     * "4pm" on the same day as /from).
     */
    @Override
    public boolean occursOn(LocalDate date) {
        LocalDate fromDate = from.getDate();
        LocalDate toDate = to.getDate();
        if (fromDate != null && toDate != null) {
            return !date.isBefore(fromDate) && !date.isAfter(toDate);
        } else if (fromDate != null) {
            return fromDate.equals(date);
        } else if (toDate != null) {
            return toDate.equals(date);
        }
        return false;
    }

    /**
     * Two events clash when both have a fully recognized [from, to]
     * date range and those ranges overlap on at least one day. Only
     * Event vs Event clashes are checked: a Todo has no schedule at
     * all, and a Deadline is a single instant rather than a span, so
     * neither can meaningfully "overlap" with anything. An event with
     * free-text (unrecognized) dates is skipped rather than guessed
     * at, the same way {@link #occursOn(LocalDate)} treats it.
     *
     * @param other The other task to check against.
     * @return Whether this event's date range overlaps {@code other}'s.
     */
    @Override
    public boolean clashesWith(Task other) {
        if (!(other instanceof Event)) {
            return false;
        }
        Event otherEvent = (Event) other;
        LocalDate thisFrom = from.getDate();
        LocalDate thisTo = to.getDate();
        LocalDate otherFrom = otherEvent.from.getDate();
        LocalDate otherTo = otherEvent.to.getDate();
        if (thisFrom == null || thisTo == null || otherFrom == null || otherTo == null) {
            return false;
        }
        return !thisFrom.isAfter(otherTo) && !otherFrom.isAfter(thisTo);
    }
}
