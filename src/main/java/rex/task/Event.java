package rex.task;

import java.time.LocalDate;

/**
 * A task that spans a stretch of time, from one date to another. Either end
 * may carry a time of day, so an event can be a meeting on one afternoon or a
 * trip lasting several days.
 */
public class Event extends Task {
    private final TaskDateTime from;
    private final TaskDateTime to;

    /**
     * Creates an event.
     *
     * @param description what is happening.
     * @param from when it starts.
     * @param to when it ends.
     */
    public Event(String description, TaskDateTime from, TaskDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /** Returns "E", the letter marking an event in a listed or saved task. */
    @Override
    public String getTypeIcon() {
        return "E";
    }

    /** Returns both ends in brackets, e.g. " (from: Oct 15 2019 to: Oct 16 2019)". */
    @Override
    public String getDetails() {
        return " (from: " + from + " to: " + to + ")";
    }

    /**
     * An event falls on every day it covers, not just the day it starts, so a
     * retreat running from the 18th to the 20th is found on the 19th too. Both
     * ends count as part of the event, hence the two "not outside" tests
     * rather than a strict comparison.
     */
    @Override
    public boolean isOn(LocalDate date) {
        return !date.isBefore(from.getDate()) && !date.isAfter(to.getDate());
    }

    /**
     * Note that this writes both dates in their save format, not the one shown
     * to the user, so that the line can be read back by TaskDateTime.parse.
     */
    @Override
    public String toSaveFormat() {
        return super.toSaveFormat() + FIELD_SEPARATOR + from.toSaveFormat()
                + FIELD_SEPARATOR + to.toSaveFormat();
    }
}
