package nova;

import java.time.LocalDateTime;

/**
 * A task that runs between two points in time, such as
 * "project meeting (from: Oct 15 2019, 2:00PM to: Oct 15 2019, 4:00PM)".
 * Both ends are stored as LocalDateTime values rather than text, so the format
 * the user types is independent of the format shown back to them.
 */
public class Event extends Task {
    protected final LocalDateTime from;
    protected final LocalDateTime to;

    /**
     * Creates an event running between the two given times.
     *
     * @param taskName what the user typed as the description.
     * @param isMarked whether the task starts out done.
     * @param from     when the event starts.
     * @param to       when the event ends.
     */
    public Event(String taskName, boolean isMarked, LocalDateTime from, LocalDateTime to) {
        super(taskName, isMarked);
        this.from = from;
        this.to = to;
    }

    /**
     * Reports whether this event overlaps another in time.
     * The comparison is half-open, so an event ending exactly when another
     * begins does not clash: back-to-back scheduling is normal. The same rule
     * means a zero-length event never clashes with anything, including itself.
     *
     * @param other the event to compare against.
     * @return true if the two events occupy any of the same time.
     */
    public boolean clashesWith(Event other) {
        // A zero-length event occupies no time, so it overlaps nothing. The
        // comparison below cannot express that on its own: it would report an
        // overlap for an instant falling strictly inside another event's span.
        if (!this.occupiesTime() || !other.occupiesTime()) {
            return false;
        }

        return this.from.isBefore(other.to) && other.from.isBefore(this.to);
    }

    /**
     * Reports whether this event covers any time at all, which is false when
     * it starts and ends at the same instant.
     *
     * @return true if the event has a non-zero duration.
     */
    private boolean occupiesTime() {
        return this.from.isBefore(this.to);
    }

    /**
     * Returns this task as one data-file line.
     *
     * @return the saved form, e.g.
     *         "E | 0 | meeting | 2019-10-15T14:00 | 2019-10-15T16:00". Dates are
     *         written in ISO-8601 so that they read back exactly.
     */
    @Override
    public String toDataString() {
        return "E | " + dataFields() + " | " + this.from + " | " + this.to;
    }

    /**
     * Returns this task in the form shown to the user.
     *
     * @return the display form, e.g.
     *         "[E][ ] meeting (from: Oct 15 2019, 2:00PM to: Oct 15 2019, 4:00PM)".
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + String.format(" (from: %s to: %s)",
                formatDateTime(this.from), formatDateTime(this.to));
    }
}
