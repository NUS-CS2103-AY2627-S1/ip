package kiki.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that spans a start and end date/time.
 */
public class Events extends Task {
    private static final DateTimeFormatter OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a");

    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Creates a new event task.
     *
     * @param description Description of the task.
     * @param from Start date/time of the event.
     * @param to End date/time of the event.
     */
    public Events(String description, LocalDateTime from, LocalDateTime to) {
        super(description, TaskType.EVENT);
        this.from = from;
        this.to = to;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    @Override
    public String toString() {
        return super.toString() + " (from: " + from.format(OUTPUT_FORMAT)
                + " to: " + to.format(OUTPUT_FORMAT) + ")";
    }
}
