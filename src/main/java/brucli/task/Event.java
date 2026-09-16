package brucli.task;

import java.time.LocalDateTime;

/**
 * Represents a scheduled task with start and end times.
 */
public class Event extends Task {
    private final LocalDateTime start;
    private final LocalDateTime end;

    /**
     * Creates an event with its internal ID, description, and time range.
     */
    public Event(
            int id,
            String description,
            LocalDateTime start,
            LocalDateTime end
    ) {
        super(id, description);
        this.start = start;
        this.end = end;
    }

    @Override
    public String serialize() {
        return String.format(
                "E | %d | %s | %s | %s",
                isDone ? 1 : 0,
                description,
                DateTimes.serialize(start),
                DateTimes.serialize(end)
        );
    }

    @Override
    protected String getType() {
        return "E";
    }

    @Override
    protected LocalDateTime getDateTime() {
        return start;
    }

    @Override
    public String toString() {
        return super.toString()
                + " (from: " + DateTimes.display(start)
                + " to: " + DateTimes.display(end) + ")";
    }
}
