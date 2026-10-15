package clover.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task with a start date and end date.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy");

    private final LocalDate start;
    private final LocalDate end;

    /**
     * Creates an event with a description, start date, and end date.
     */
    public Event(String description, LocalDate start, LocalDate end) {
        super(description);
        this.start = start;
        this.end = end;
    }

    /**
     * Creates an event that is optionally linked to a tutoree.
     */
    public Event(String description, LocalDate start, LocalDate end, String tutoreeName) {
        super(description, tutoreeName);
        this.start = start;
        this.end = end;
    }

    /** Returns the event start text. */
    public LocalDate getStart() {
        return start;
    }

    /**
     * Returns the event end text.
     */
    public LocalDate getEnd() {
        return end;
    }

    /**
     * Returns this event in Clover's display format.
     */
    @Override
    public String toString() {
        return "[E] " + formatTaskDisplay()
                + " (from: " + start.format(DISPLAY_FORMAT)
                + " to: " + end.format(DISPLAY_FORMAT) + ")" + formatTutoreeSuffix();
    }
}
