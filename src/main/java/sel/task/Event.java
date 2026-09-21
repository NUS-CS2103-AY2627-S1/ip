package sel.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import sel.exception.SelException;

/**
 * Represents a task that occurs over a fixed time range, from a start
 * date/time to an end date/time.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
        DateTimeFormatter.ofPattern("MMM d yyyy, h:mma", Locale.ENGLISH);

    protected LocalDateTime from;
    protected LocalDateTime to;

    /**
     * Creates a new, not-done event task.
     *
     * <p>The time range is checked here rather than by the caller, so that
     * an event which ends before it starts cannot be created at all -
     * whether it came from a typed command or from a hand-edited save file.
     *
     * @param description text describing the event.
     * @param from the date and time the event starts.
     * @param to the date and time the event ends.
     * @throws SelException if either date/time is missing, or if the event
     *     does not start strictly before it ends.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) throws SelException {
        super(description);

        if (from == null || to == null) {
            throw new SelException("Bro, an event needs both a start and an end date/time.");
        }
        if (from.isEqual(to)) {
            throw new SelException("Bro, an event can't start and end at the same moment ("
                    + from.format(DISPLAY_FORMAT) + "). Give it some length.");
        }
        if (from.isAfter(to)) {
            throw new SelException("Bro, that event ends before it starts: "
                    + from.format(DISPLAY_FORMAT) + " to " + to.format(DISPLAY_FORMAT)
                    + ". Swap them round?");
        }

        this.from = from;
        this.to = to;
    }

    /**
     * Returns the date and time this event starts.
     *
     * @return the event's start date and time.
     */
    public LocalDateTime getFrom() {
        return this.from;
    }

    /**
     * Returns the date and time this event ends.
     *
     * @return the event's end date and time.
     */
    public LocalDateTime getTo() {
        return this.to;
    }

    /**
     * Checks whether another task is an event with the same description
     * and exactly the same start and end date/times.
     *
     * @param other the task to compare against, possibly {@code null}.
     * @return {@code true} if both events describe the same thing.
     */
    @Override
    public boolean hasSameDetailsAs(Task other) {
        // The superclass already checked that other is an Event, so the
        // casts below cannot fail.
        return super.hasSameDetailsAs(other)
                && this.from.equals(((Event) other).from)
                && this.to.equals(((Event) other).to);
    }

    /**
     * Returns the display form of this event, e.g.
     * {@code "[E][ ] meeting(from:Dec 2 2019, 2:00PM to:Dec 2 2019, 4:00PM)"}.
     *
     * @return the formatted string representation of this event.
     */
    @Override
    public String toString() {
        return "[E][" + this.getStatusIcon() + "] " + this.description
                + "(from:" + this.from.format(DISPLAY_FORMAT) + " to:" + this.to.format(DISPLAY_FORMAT) + ")";
    }
}
