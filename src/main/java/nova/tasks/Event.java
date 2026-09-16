package nova.tasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents an event occurring between a start and end time.
 */
public class Event extends Task {

    private LocalDateTime from;
    private LocalDateTime to;

    /**
     * Creates an event.
     *
     * @param description description of the event
     * @param from start date and time
     * @param to end date and time
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
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
        DateTimeFormatter outputFormat =
                DateTimeFormatter.ofPattern(
                        "MMM dd yyyy, h:mm a",
                        Locale.ENGLISH);

        return "[E]" + super.toString()
                + " (from: " + from.format(outputFormat)
                + " to: " + to.format(outputFormat) + ")";
    }

    @Override
    public String toFileString() {
        return "E | " + (isDone ? "X" : "O")
                + " | " + description
                + " | " + from
                + " | " + to;
    }
}
