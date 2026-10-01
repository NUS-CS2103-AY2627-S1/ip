package bo.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task with a start and end date and time.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Creates an event task.
     *
     * @param description The event description.
     * @param from The start date and time.
     * @param to The end date and time.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns this event in the task-list format.
     *
     * @return The formatted event.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + formatDateTime(from)
                + " to: " + formatDateTime(to) + ")";
    }

    /**
     * Returns this event in the save-file format.
     *
     * @return The saved event text.
     */
    @Override
    public String toFileString() {
        return "E | " + getSaveStatus() + " | " + getDescription() + " | "
                + from + " | " + to;
    }

    /**
     * Formats an event date and time for display.
     *
     * @param dateTime The date and time to format.
     * @return The formatted date and time.
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DISPLAY_DATE_TIME_FORMAT);
    }
}
