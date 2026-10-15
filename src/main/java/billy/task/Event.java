package billy.task;

import java.time.LocalDate;

import billy.BillyException;

/**
 * Represents a task that runs from one date or time until another.
 *
 * <p>Both ends are real dates rather than free text, which lets Billy work out
 * the days an event covers,
 * e.g. {@code [E][ ] project meeting (from: Dec 2 2019, 2:00pm to: Dec 2 2019, 4:00pm)}.
 */
public class Event extends Task {

    /** Marks a saved line as an event. */
    public static final String TYPE_LETTER = "E";

    /** How many fields a saved event has: a todo's three, plus a start and an end. */
    public static final int FIELD_COUNT = 5;

    /** When the event starts. */
    protected TaskDate from;

    /** When the event ends. */
    protected TaskDate to;

    /**
     * Creates an event that is not done yet.
     *
     * <p>An event that ends before it starts is refused rather than stored. Such
     * an event covers no days at all, so it would sit in the list yet never be
     * found by {@code on} — including on the very days it names, which would
     * look like a fault in the search rather than in the event.
     *
     * @param description what the user wants to do
     * @param from when the event starts
     * @param to when the event ends
     * @throws BillyException if the event would end before it starts, or would
     *                        begin and end at the same moment
     */
    public Event(String description, TaskDate from, TaskDate to) throws BillyException {
        super(description);
        if (to.isBefore(from)) {
            throw new BillyException("An event can't end before it starts, and you gave"
                    + " from: " + from + " to: " + to + ".");
        }
        // An event that begins and ends at the same moment covers no time at
        // all. The hour has to have been given for that to be what the user
        // meant: "/from 2019-12-02 /to 2019-12-02" is an ordinary all-day
        // event, and refusing that would be wrong.
        if (from.hasTime() && from.isSameInstant(to)) {
            throw new BillyException("An event has to last some time, and you gave"
                    + " from: " + from + " to: " + to + ". Leave the times off if you"
                    + " mean the whole day.");
        }
        this.from = from;
        this.to = to;
    }

    /**
     * Returns whether another task is the same event, which requires the same
     * period as well as the same wording.
     */
    @Override
    public boolean isSameTask(Task other) {
        if (!super.isSameTask(other)) {
            return false;
        }
        Event otherEvent = (Event) other;
        return from.isSameInstant(otherEvent.from) && to.isSameInstant(otherEvent.to);
    }

    /**
     * Returns whether the given day falls within this event, which covers every
     * day it runs across rather than only the day it starts.
     *
     * <p>Both ends count as part of it, so an event running from the 2nd to the
     * 4th is found by asking about the 2nd, the 3rd or the 4th.
     */
    @Override
    public boolean occursOn(LocalDate day) {
        return !day.isBefore(from.getDate()) && !day.isAfter(to.getDate());
    }

    /**
     * Returns this event as one line of the save file, with the start and end as
     * fields of their own,
     * e.g. {@code E | 0 | project meeting | 2019-12-02 1400 | 2019-12-02 1600}.
     */
    @Override
    public String toSaveFormat() {
        return TYPE_LETTER + FIELD_SEPARATOR + super.toSaveFormat()
                + FIELD_SEPARATOR + from.toSaveFormat() + FIELD_SEPARATOR + to.toSaveFormat();
    }

    /**
     * Returns this event as it is shown, with both ends after it, e.g.
     * {@code [E][ ] project meeting (from: Dec 2 2019, 2:00pm to: Dec 2 2019, 4:00pm)}.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
