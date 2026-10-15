package gepit.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task occurring between two specific dates.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy");

    private final LocalDate start;
    private final LocalDate end;

    /**
     * Creates an event with the specified description, start date, and end date.
     */
    public Event(String description, LocalDate start, LocalDate end) {
        super(description);
        this.start = start;
        this.end = end;
    }

    public LocalDate getStart() {
        return start;
    }

    public LocalDate getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + start.format(DISPLAY_FORMAT)
                + " to: " + end.format(DISPLAY_FORMAT) + ")";
    }

    @Override
    public String toDataString() {
        return "E | " + super.toDataString()
                + " | " + start
                + " | " + end;
    }
}
