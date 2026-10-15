package elsa.task;

import java.time.LocalDate;

import elsa.Dates;

/**
 * A task that runs between two stated dates,
 * for example "project meeting (from: Oct 15 2019 to: Oct 16 2019)".
 */
public class Event extends Task {
    /** The date the event starts, held as a date rather than as text. */
    protected LocalDate from;

    /** The date the event ends, held as a date rather than as text. */
    protected LocalDate to;

    /**
     * Creates an event that is not done yet.
     *
     * @param description the text describing what happens
     * @param from        the date it starts
     * @param to          the date it ends
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns whether this event falls on the given date, which it does on its
     * first and last days and on every day between them.
     *
     * @param date the date being asked about
     * @return true if the event is running on that date
     */
    @Override
    public boolean occursOn(LocalDate date) {
        // Written as "not before the start and not after the end" so that the
        // first and last days count as part of the event, which isBefore and
        // isAfter on their own would leave out.
        return !date.isBefore(from) && !date.isAfter(to);
    }

    @Override
    public boolean isSameTask(Task other) {
        if (!super.isSameTask(other)) {
            return false;
        }
        // The cast is safe: the inherited check has already confirmed the kinds match.
        Event otherEvent = (Event) other;
        return from.equals(otherEvent.from) && to.equals(otherEvent.to);
    }

    /**
     * Returns the event as it should appear to the user,
     * for example "[E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)".
     *
     * @return the event marker, the inherited task text, and both times
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + Dates.format(from)
                + " to: " + Dates.format(to) + ")";
    }

    /**
     * Returns the event as one line of the data file,
     * for example "E | 0 | project meeting | 2019-10-15 | 2019-10-16".
     * The two dates are kept in separate fields rather than as one piece of text,
     * so that reading the file back does not have to split them apart again.
     *
     * @return the event's type letter, the inherited fields, and both times
     */
    @Override
    public String toSaveFormat() {
        return TaskFormat.EVENT + TaskFormat.SEPARATOR + super.toSaveFormat()
                + TaskFormat.SEPARATOR + Dates.toSaveFormat(from)
                + TaskFormat.SEPARATOR + Dates.toSaveFormat(to);
    }
}
