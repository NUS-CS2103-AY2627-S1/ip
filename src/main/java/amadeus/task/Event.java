package amadeus.task;

import java.time.LocalDate;

/** A task that runs from one date and time to another. */
public class Event extends Task {

    /** When the event begins. */
    protected TaskDateTime start;

    /** When the event ends; never earlier than the start. */
    protected TaskDateTime end;

    /**
     * Creates an event that starts off not done.
     * <p>
     * The two dates are stored in the order given; the caller is responsible for
     * having read them from the user's "/from" and "/to" the right way round.
     *
     * @param description what the event is, e.g. "team project".
     * @param start       when the event begins.
     * @param end         when the event ends.
     */
    public Event(String description, TaskDateTime start, TaskDateTime end) {
        super(description);
        this.start = start;
        this.end = end;
    }

    /** Returns the date the event starts. */
    public TaskDateTime getStart() {
        return start;
    }

    /** Returns the date the event ends. */
    public TaskDateTime getEnd() {
        return end;
    }

    /**
     * An event is relevant to every day it spans, not only the day it starts,
     * so a three-day event shows up on all three days.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(start.getDate()) && !date.isAfter(end.getDate());
    }

    /**
     * Returns the event as the user sees it, e.g.
     * "[E][ ] team project (from: Dec 01 2019 to: Dec 03 2019)".
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + start + " to: " + end + ")";
    }
}
