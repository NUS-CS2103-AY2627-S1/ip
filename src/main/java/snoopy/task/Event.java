package snoopy.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that occurs between a start and end time.
 */
public class Event extends Task {
    /** Format used to present stored dates to the user. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private final LocalDate from;
    private final LocalDate to;

    /**
     * Creates an incomplete event task.
     *
     * @param description Text describing the event.
     * @param from Start date.
     * @param to End date.
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        assert from != null : "Event start date must be provided";
        assert to != null : "Event end date must be provided";
        this.from = from;
        this.to = to;
    }

    /**
     * Converts this event into one line of saved data.
     *
     * @return Saved event data.
     */
    @Override
    public String toDataString() {
        return "E | " + super.toDataString() + " | " + this.from + " | " + this.to;
    }

    /**
     * Returns this task with its type, start time, and end time.
     *
     * @return The formatted event task.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + from.format(DISPLAY_DATE_FORMAT)
                + " to: " + to.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
