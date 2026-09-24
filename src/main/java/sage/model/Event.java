package sage.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Represents a task that takes place during a specified time range. */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM d yyyy, hh:mma", Locale.ENGLISH);

    private final LocalDateTime from;
    private final LocalDateTime to;

    /** Creates an event task.
     *
     * @param description event description
     * @param from event start date and time
     * @param to event end date and time
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /** Returns the event start date and time.
     *
     * @return event start date and time
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /** Returns the event end date and time.
     *
     * @return event end date and time
     */
    public LocalDateTime getTo() {
        return to;
    }

    /** Returns this event in Sage's display format. */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + from.format(DISPLAY_FORMATTER)
                + " to: " + to.format(DISPLAY_FORMATTER) + ")";
    }
}
