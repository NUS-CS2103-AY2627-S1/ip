package duncan.task;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that starts on a specific date and ends on a
 * specific date, e.g. "project fair from 2019-12-01 to 2019-12-02".
 * The dates are stored as {@link LocalDate}, so they are validated on
 * input and can be displayed in a friendlier format than typed in.
 */
public class Event extends Task {
    /** Format the dates are displayed in, e.g. "Dec 2 2019". */
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy");

    protected LocalDate from;
    protected LocalDate to;

    /**
     * Creates an event that is not done yet.
     *
     * @param description What the task is, as typed by the user.
     * @param from The date this event starts.
     * @param to The date this event ends.
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        assert from != null;
        assert to != null;
        this.from = from;
        this.to = to;
    }

    /**
     * Moves this event to new start and end dates. Whether it is done is unchanged.
     *
     * @param newFrom The new date this event starts.
     * @param newTo The new date this event ends.
     */
    public void reschedule(LocalDate newFrom, LocalDate newTo) {
        assert newFrom != null;
        assert newTo != null;
        from = newFrom;
        to = newTo;
    }

    /**
     * Pushes both of this event's dates back by the given number of days.
     * Whether it is done is unchanged.
     *
     * @param days How many days later the event starts and ends, at least 1.
     * @throws DateTimeException If either new date is beyond the latest supported date,
     *     in which case neither date is changed.
     */
    public void snooze(int days) throws DateTimeException {
        assert days >= 1;
        // Work out both dates before changing either, so a failure leaves the event as it was.
        LocalDate newFrom = from.plusDays(days);
        LocalDate newTo = to.plusDays(days);
        from = newFrom;
        to = newTo;
    }

    /** Returns whether {@code other} is the same task, as {@link Task} defines it, with the same dates. */
    @Override
    public boolean isDuplicateOf(Task other) {
        return super.isDuplicateOf(other) && other instanceof Event event
                && from.equals(event.from) && to.equals(event.to);
    }

    /** Returns this event as a file line, e.g. "E\t0\tproject fair\t2019-12-01\t2019-12-02". */
    @Override
    public String toFileFormat() {
        return "E" + FIELD_SEPARATOR + getSharedFileFields()
                + FIELD_SEPARATOR + from + FIELD_SEPARATOR + to;
    }

    /** Returns this event for display, e.g. "[E][ ] project fair (from: Dec 1 2019 to: Dec 2 2019)". */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from.format(DISPLAY_FORMAT)
                + " to: " + to.format(DISPLAY_FORMAT) + ")";
    }
}
