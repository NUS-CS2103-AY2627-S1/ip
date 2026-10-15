package jaylen;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/** A task that takes place at a stated time. */
public class Event extends Task {
    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Creates an event.
     *
     * @param description what the event is
     * @param from the event start date and time
     * @param to the event end date and time
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description, TaskType.EVENT);
        assert from != null : "event start should not be null";
        assert to != null : "event end should not be null";
        if (!from.isBefore(to)) {
            throw new JaylenException("The event end time must be after the start time.");
        }
        this.from = from;
        this.to = to;
    }

    /**
     * Creates an event from user input.
     *
     * @param description what the event is
     * @param from the user-provided event start date and time
     * @param to the user-provided event end date and time
     */
    public Event(String description, String from, String to) {
        this(description, parseDateTime(from), parseDateTime(to));
    }

    /**
     * Returns the event start as a typed date and time.
     *
     * @return the event start date and time
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns the event end as a typed date and time.
     *
     * @return the event end date and time
     */
    public LocalDateTime getTo() {
        return to;
    }

    @Override
    public String toString() {
        return super.toString() + " (from: " + DateTimeParser.formatDateTime(from)
                + " to: " + DateTimeParser.formatDateTime(to) + ")";
    }

    private static LocalDateTime parseDateTime(String text) {
        try {
            return DateTimeParser.parseDateTime(text);
        } catch (DateTimeParseException exception) {
            throw new JaylenException("Use event times such as 2019-10-15 1800.");
        }
    }
}
