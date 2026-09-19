package leo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that spans a start and end date/time.
 */
public class Event extends Task {
    /** The date/time this event starts. */
    private LocalDateTime from;
    /** The date/time this event ends. */
    private LocalDateTime to;
    private static final DateTimeFormatter INPUT_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    private static final DateTimeFormatter OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm");

    /**
     * Returns "AM" or "PM" for the given date/time, computed directly
     * from the hour rather than relying on {@link DateTimeFormatter}'s
     * locale-dependent "a" pattern, whose letter case can vary between
     * JDK versions.
     *
     * @param dateTime the date/time to check
     * @return "AM" if before noon, "PM" otherwise
     */
    private static String amOrPm(LocalDateTime dateTime) {
        return dateTime.getHour() < 12 ? "AM" : "PM";
    }

    /**
     * Creates a new, not-done event.
     *
     * @param description description of the event
     * @param from start date/time, in "yyyy-MM-dd HHmm" format
     * @param to end date/time, in "yyyy-MM-dd HHmm" format
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = LocalDateTime.parse(from, INPUT_FORMAT);
        this.to = LocalDateTime.parse(to, INPUT_FORMAT);
    }

    /**
     * Returns the type code for an event.
     *
     * @return "E"
     */
    @Override
    public String getType() {
        return "E";
    }

    /**
     * Returns this event encoded as a save-file line, with the start
     * and end date/time appended after the fields from {@link Task}.
     *
     * @return the save-file line for this event
     */
    @Override
    public String toFileString() {
        return super.toFileString()
                + " | " + from.format(INPUT_FORMAT)
                + " | " + to.format(INPUT_FORMAT);
    }

    /**
     * Returns this event formatted for display, including the start
     * and end date/time.
     *
     * @return the display string for this event
     */
    @Override
    public String toString() {
        return super.toString()
                + " (from: " + from.format(OUTPUT_FORMAT) + amOrPm(from)
                + " to: " + to.format(OUTPUT_FORMAT) + amOrPm(to) + ")";
    }
}
