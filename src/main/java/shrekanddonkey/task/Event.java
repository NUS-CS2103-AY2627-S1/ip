package shrekanddonkey.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that takes place between a start and an end date or time.
 */
public class Event extends Task {
    private static final DateTimeFormatter OUTPUT_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a", Locale.ENGLISH);
    /** Event start date and time. */
    private LocalDateTime from;
    /** Event end date and time. */
    private LocalDateTime to;

    /**
     * Creates an event that is initially not done.
     *
     * @param description description of the task.
     * @param from start date and time.
     * @param to end date and time.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        assert from != null : "Event start date/time cannot be null";
        assert to != null : "Event end date/time cannot be null";
        assert !to.isBefore(from) : "Event end time cannot be before start time";
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event start date and time.
     *
     * @return event start date and time.
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns the event end date and time.
     *
     * @return event end date and time.
     */
    public LocalDateTime getTo() {
        return to;
    }

    /**
     * Returns this task with its event type marker and timing text.
     *
     * @return formatted event text.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from.format(OUTPUT_FORMATTER)
                + " to: " + to.format(OUTPUT_FORMATTER) + ")";
    }
}
