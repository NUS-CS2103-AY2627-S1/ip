package megatron.task;

import java.time.Duration;
import java.time.LocalDateTime;

/** Represents a task with a start time and an end time. */
public class Event extends Task {
    private LocalDateTime from;
    private LocalDateTime to;

    /**
     * Creates an event task.
     *
     * @param description the text describing the event
     * @param from the event's start time text
     * @param to the event's end time text
     */
    public Event(String description, String from, String to) {
        this(description, DateTimeParser.parse(from), DateTimeParser.parse(to));
    }

    /** Creates an event task with typed start and end dates and times. */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        if (!from.isBefore(to)) {
            throw new IllegalArgumentException("event start must be before event end");
        }
        this.from = from;
        this.to = to;
    }

    /** Moves this event so that it starts at the supplied date and time. */
    public void reschedule(String newStart) {
        LocalDateTime updatedFrom = DateTimeParser.parse(newStart);
        Duration duration = Duration.between(from, to);
        if (duration.isZero() || duration.isNegative()) {
            throw new IllegalStateException("event duration is invalid");
        }
        from = updatedFrom;
        to = updatedFrom.plus(duration);
    }

    @Override
    public String toString() {
        return "[E][" + getStatusIcon() + "] " + description + " (from: " + DateTimeParser.format(from)
                + " to: " + DateTimeParser.format(to) + ")";
    }
}
