package bobby.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import bobby.storage.Storage;

/**
 * A task that takes place between a start and end date and time.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu h:mm a", Locale.ENGLISH);

    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;

    /**
     * Creates an event task.
     *
     * @param description the event description
     * @param startDateTime the start date and time
     * @param endDateTime the end date and time
     */
    public Event(String description, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        super(description);
        assert startDateTime != null : "Event start date and time must not be null";
        assert endDateTime != null : "Event end date and time must not be null";
        assert startDateTime.isBefore(endDateTime) : "Event start must be before its end";
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    /**
     * Returns whether this event has the same description and time range as another task.
     *
     * @param other the task to compare with.
     * @return {@code true} when both tasks represent the same event.
     */
    @Override
    public boolean hasSameDetailsAs(Task other) {
        return super.hasSameDetailsAs(other)
                && startDateTime.equals(((Event) other).startDateTime)
                && endDateTime.equals(((Event) other).endDateTime);
    }

    /**
     * Returns this event in the on-disk format used by {@link Storage}.
     *
     * @return a line that represents this event in the save file
     */
    @Override
    public String toFileString() {
        return "E | " + (isDone ? "1" : "0") + " | " + description
                + " | " + startDateTime + " | " + endDateTime
                + " | " + getCompletionDateTimeFileValue();
    }

    /**
     * Returns this event task in the standard list format.
     *
     * @return the formatted event task
     */
    @Override
    public String toString() {
        return "[E][" + getStatusIcon() + "] " + description
                + " (commencing: " + startDateTime.format(DISPLAY_DATE_FORMAT)
                + "; concluding: " + endDateTime.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
