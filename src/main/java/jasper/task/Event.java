package jasper.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents an event task that starts and ends at specific times.
 */
public class Event extends Task {
    /** Start time of the event */
    private final LocalDateTime from;
    /** End time of the event */
    private final LocalDateTime to;

    /**
     * Constructs an Event task with a description, start time, and end time.
     *
     * @param description Description of the event.
     * @param from Start date and time.
     * @param to End date and time.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    public String getFrom() {
        return from.toString();
    }

    public String getTo() {
        return to.toString();
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm");
        return String.format("[E]%s (from: %s to: %s)", super.toString(), from.format(fmt), to.format(fmt));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Event other) {
            return super.equals(other) && this.from.equals(other.from) && this.to.equals(other.to);
        }
        return false;
    }
}
