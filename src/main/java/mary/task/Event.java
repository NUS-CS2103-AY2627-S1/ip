package mary.task;

import java.time.LocalDateTime;

/**
 * Represents a task with a start time and an end time.
 */
public class Event extends Task {
    protected LocalDateTime from;
    protected LocalDateTime to;

    /**
     * Creates an unfinished event task without validating endpoint order.
     *
     * @param description task text.
     * @param from local start date/time.
     * @param to local end date/time.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Formats the task for console display with its type and completion markers.
     *
     * @return task description and formatted date/time details.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + formatDateTime(from) + " to: "
                + formatDateTime(to) + ")";
    }

    /**
     * Serializes the type, completion flag, description, and ISO date/time fields.
     *
     * @return pipe-delimited record for storage; descriptions are not escaped.
     */
    @Override
    public String toStorageRecord() {
        return "E | " + (isDone ? "1" : "0") + " | " + description + " | " + from + " | " + to;
    }

    /**
     * Returns the event start date/time.
     *
     * @return the local start date/time.
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns the event end date/time.
     *
     * @return the local end date/time.
     */
    public LocalDateTime getTo() {
        return to;
    }
}
