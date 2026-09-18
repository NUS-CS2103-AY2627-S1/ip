package aster.task;

import java.time.LocalDate;

/**
 * A task that spans a stated start and end date.
 *
 * <p>Both endpoints are held as real dates, for the same reason as in
 * {@link Deadline}: Aster can then tell a date from any other wording and show it in
 * a friendlier form than the one it is typed in.
 *
 * <p>An event is not required to span more than one day: an event that starts and ends
 * on the same date is an ordinary whole-day event.
 *
 * <p>This class accepts whatever endpoints it is given, including an end before the
 * start. Refusing that pair is a check on what the user types, so it lives in the parser;
 * keeping it out of here is what lets a file holding such an event still be read back
 * rather than locking the user out of their saved tasks.
 */
public class Event extends Task {
    private final LocalDate from;
    private final LocalDate to;

    /**
     * Creates an event with the given description and endpoints, initially not done.
     *
     * @param description text describing the task.
     * @param from the date the event starts on.
     * @param to the date the event ends on.
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the start date of this event.
     *
     * @return the date the event starts on.
     */
    public LocalDate getFrom() {
        return from;
    }

    /**
     * Returns the end date of this event.
     *
     * @return the date the event ends on.
     */
    public LocalDate getTo() {
        return to;
    }

    /**
     * Returns this event as its type marker, the inherited status and
     * description, and both endpoints, for example
     * {@code [E][ ] project meeting (from: Aug 06 2019 to: Aug 08 2019)}.
     *
     * @return the display form of this event.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + TaskDates.format(from)
                + " to: " + TaskDates.format(to) + ")";
    }
}
