package jelly.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that takes place during a specified time range.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm");

    /** The event's starting time. */
    private final LocalDateTime from;
    /** The event's ending time. */
    private final LocalDateTime to;

    /**
     * Creates an incomplete event task.
     *
     * @param description the text describing the event.
     * @param from the event's starting time.
     * @param to the event's ending time.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        if (from == null || to == null || !to.isAfter(from)) {
            throw new IllegalArgumentException("Event end time must be after its start time.");
        }
        this.from = from;
        this.to = to;
    }

    /** Returns the event's starting time. */
    public LocalDateTime getFrom() {
        return this.from;
    }

    /** Returns the event's ending time. */
    public LocalDateTime getTo() {
        return this.to;
    }

    @Override
    protected String getTaskType() {
        return "E";
    }

    @Override
    protected String getTaskDetails() {
        return " (from: " + this.from.format(DISPLAY_FORMAT)
                + " to: " + this.to.format(DISPLAY_FORMAT) + ")";
    }
}
