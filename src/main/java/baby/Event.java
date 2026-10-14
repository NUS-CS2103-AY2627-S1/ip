package baby;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that happens from a start date or time to an end date or time.
 */
public class Event extends Task {
    private LocalDateTime from;
    private LocalDateTime to;

    /**
     * Creates an event task with the given description, start, and end.
     *
     * @param description Description of the event task.
     * @param from Start date or time text.
     * @param to End date or time text.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String getTypeIcon() {
        return "E";
    }

    @Override
    public String toFileString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

        return "E | " + (isDone ? "1" : "0") + " | " + description
                + " | " + from.format(formatter) + " | " + to.format(formatter);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a");

        return super.toString() + " (from: " + from.format(formatter) + " to: " + to.format(formatter) + ")";
    }
}
