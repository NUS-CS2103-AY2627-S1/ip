package rei.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that takes place between two dates and times.
 */
public class Events extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a", Locale.ENGLISH);
    private LocalDateTime start;
    private LocalDateTime end;

    /**
     * Creates an incomplete event with the specified time range.
     *
     * @param description Description of the event.
     * @param start Date and time at which the event starts.
     * @param end Date and time at which the event ends.
     */
    public Events(String description, LocalDateTime start, LocalDateTime end) {
        super(description, TaskType.EVENT);
        this.start = start;
        this.end = end;
    }

    /** Returns the event's starting date and time. */
    public LocalDateTime getStart() {
        return this.start;
    }

    /**
     * Updates the event's starting date and time.
     *
     * @param start New starting date and time.
     */
    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    /** Returns the event's ending date and time. */
    public LocalDateTime getEnd() {
        return this.end;
    }

    /**
     * Updates the event's ending date and time.
     *
     * @param end New ending date and time.
     */
    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return super.toString() + " (from: " + start.format(DISPLAY_FORMAT)
                + " to: " + end.format(DISPLAY_FORMAT) + ")";
    }
}
