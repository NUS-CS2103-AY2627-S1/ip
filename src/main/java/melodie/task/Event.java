package melodie.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that occurs between a start date and an end date.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd uuuu, h:mm a", Locale.ENGLISH);
    private static final String IDENTIFIER = "[E]";
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;

    /**
     * Creates an event task with the specified description, start date, and end date.
     *
     * @param description Description of the event task.
     * @param startDateTime Date and time at which the event starts.
     * @param endDateTime Date and time at which the event ends.
     */
    public Event(String description, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        super(description);
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    /**
     * Creates an event from the ISO date-time format used in the storage file.
     *
     * @param description Description of the event task.
     * @param startDateTime Start date and time in ISO format.
     * @param endDateTime End date and time in ISO format.
     */
    public Event(String description, String startDateTime, String endDateTime) {
        this(description, LocalDateTime.parse(startDateTime), LocalDateTime.parse(endDateTime));
    }

    public LocalDateTime getStartDateTime() {
        return this.startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return this.endDateTime;
    }

    @Override
    public String toStorageString() {
        return "E | " + super.toStorageString()
                + " | " + this.startDateTime.toString()
                + " | " + this.endDateTime.toString();
    }

    @Override
    public String toString() {
        return IDENTIFIER + super.toString()
                + " (from: " + this.startDateTime.format(DISPLAY_DATE_TIME_FORMATTER)
                + " to: " + this.endDateTime.format(DISPLAY_DATE_TIME_FORMATTER) + ")";
    }
}
