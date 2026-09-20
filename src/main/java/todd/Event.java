package todd;

import java.time.LocalDateTime;

/** Represents a task that takes place between a start and end time. */
public class Event extends Task {
    protected LocalDateTime from;
    protected LocalDateTime to;

    /** Creates an incomplete event with the specified description, start, and end. */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /** Returns the date and time at which this event starts. */
    public LocalDateTime getFrom() {
        return from;
    }

    /** Returns the date and time at which this event ends. */
    public LocalDateTime getTo() {
        return to;
    }

    /** Returns a display-ready representation of this event. */
    @Override
    public String toString() {
        return "[E][" + getStatusIcon() + "] " + getDescription()
                + " (from: " + DateTimeUtil.format(from) + " to: " + DateTimeUtil.format(to) + ")";
    }
}
