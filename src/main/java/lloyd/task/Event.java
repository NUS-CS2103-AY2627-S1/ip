package lloyd.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents an event that occurs during a specified period.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy, h:mm a", Locale.ENGLISH);

    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Creates an incomplete event with a start and end date-time.
     *
     * @param description Description of the event.
     * @param from Date and time at which the event starts.
     * @param to Date and time at which the event ends.
     * @throws IllegalArgumentException If the end is before the start.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        if (to.isBefore(from)) {
            throw new IllegalArgumentException(
                    "Event end date and time cannot be before its start");
        }
        this.from = from;
        this.to = to;
    }

    /**
     * Returns when the event starts.
     *
     * @return Event start date and time.
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns when the event ends.
     *
     * @return Event end date and time.
     */
    public LocalDateTime getTo() {
        return to;
    }

    /**
     * Returns a displayable representation including the event period.
     *
     * @return Event type, completion status, description, start, and end.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + from.format(DISPLAY_FORMAT)
                + " to: " + to.format(DISPLAY_FORMAT) + ")";
    }
}
