package meka.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that occurs between specified start and end date-times.
 */
public class Event extends Task {
    /** Format used to present event date-times to the user. */
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu, h:mm a", Locale.ENGLISH);

    /** Date and time at which this event starts. */
    private final LocalDateTime from;

    /** Date and time at which this event ends. */
    private final LocalDateTime to;

    /**
     * Creates an event task.
     *
     * @param description description of the event.
     * @param from start date and time of the event.
     * @param to end date and time of the event.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        assert from != null : "An event should have a start date and time";
        assert to != null : "An event should have an end date and time";

        this.from = from;
        this.to = to;
    }

    /**
     * Returns a representation of this event suitable for saving to a file.
     *
     * Both date-times use the ISO local date-time format so they can be parsed
     * reliably when MEKA starts again.
     *
     * @return the task type, completion status, description, and ISO range.
     */
    @Override
    public String toDataString() {
        return formatDataString("E") + " | " + from + " | " + to;
    }

    /**
     * Returns the event with its type, completion status, and time range.
     *
     * @return the task formatted as "[E][ ] description
     *         (from: MMM dd yyyy, h:mm a to: MMM dd yyyy, h:mm a)".
     */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + from.format(DISPLAY_DATE_TIME_FORMAT)
                + " to: " + to.format(DISPLAY_DATE_TIME_FORMAT) + ")";
    }
}
