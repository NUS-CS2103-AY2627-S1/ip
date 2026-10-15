package rex.task;

import java.time.LocalDate;

/**
 * A task that has to be done at some point inside a stretch of time, such as
 * collecting a certificate between the 15th and the 25th.
 *
 * This carries the same two dates as an Event, but means something different
 * by them. An event is happening throughout its period, while this task is
 * happening at some unknown moment inside its period: the dates say when it
 * may be done, not when it is taking place. Keeping the two apart lets each
 * word its own listing and lets a later increment treat them differently
 * without having to guess which kind a pair of dates belonged to.
 */
public class WithinPeriod extends Task {
    private final TaskDateTime from;
    private final TaskDateTime to;

    /**
     * Creates a task to be done within a period.
     *
     * @param description what has to be done.
     * @param from the earliest point it may be done.
     * @param to the latest point it may be done.
     */
    public WithinPeriod(String description, TaskDateTime from, TaskDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /** Returns "W", the letter marking this kind of task in a listed or saved task. */
    @Override
    public String getTypeIcon() {
        return "W";
    }

    /** Returns the window in brackets, e.g. " (within: Jan 15 2026 to Jan 25 2026)". */
    @Override
    public String getDetails() {
        return " (within: " + from + " to " + to + ")";
    }

    /**
     * This task falls on every day of its window, because any of those days is
     * a day the user could still do it. Both ends count, hence the two
     * "not outside" tests rather than a strict comparison.
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
