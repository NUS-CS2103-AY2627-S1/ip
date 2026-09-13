package yappy.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that happens from one date/time to another.
 */
public class Event extends Task {
    /** Format used when showing dates to the user. */
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    /** Date on which this event starts. */
    private final LocalDate from;

    /** Date on which this event ends. */
    private final LocalDate to;

    /**
     * Creates an event task with the given description and date range.
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(TaskType.EVENT, description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toDataString() {
        return "E | " + (isDone ? "1" : "0") + " | " + encode(description)
                + " | " + encode(from.toString()) + " | " + encode(to.toString());
    }

    /**
     * Returns the event formatted with its time range text.
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + from.format(DISPLAY_FORMAT)
                + " to: " + to.format(DISPLAY_FORMAT) + ")";
    }
}
