package lebron.task;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents an Event, a Task that occurs over a start and end date/time.
 */
public class Event extends Task {
    private LocalDateTime start;
    private LocalDateTime end;

    /**
     * Constructs an Event with the given description, start, and end date/time.
     *
     * @param description Task description.
     * @param start The date/time the event starts.
     * @param end The date/time the event ends.
     */
    public Event(String description, LocalDateTime start, LocalDateTime end) {
        super(description);
        assert start != null && end != null : "Event's start and end date/time should not be null";
        this.start = start;
        this.end = end;
    }

    @Override
    public String reformat() {
        return "E | " + super.reformat() + " | " + this.start + " | " + this.end;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from " + this.start.format(DISPLAY_DATE_TIME_FORMAT)
                + " til " + this.end.format(DISPLAY_DATE_TIME_FORMAT) + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        Event other = (Event) obj;
        return start.equals(other.start) && end.equals(other.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), start, end);
    }
}
