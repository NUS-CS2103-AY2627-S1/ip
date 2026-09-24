package murphy.task;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/** A task with a specified start and end date or time. */
public class Event extends Task {
    /** The event start and end text, kept as entered by the user. */
    private final String from;
    private final String to;

    /** Creates an incomplete event task. */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /** Returns whether either ISO date endpoint falls on the supplied date. */
    public boolean occursOn(LocalDate date) {
        return isDate(from, date) || isDate(to, date);
    }

    /** Returns whether another event has the same description, start, and end values. */
    @Override
    public boolean hasSameDetailsAs(Task other) {
        return other instanceof Event event
                && normalize(description).equals(normalize(event.description))
                && normalize(from).equals(normalize(event.from))
                && normalize(to).equals(normalize(event.to));
    }

    /** Safely checks whether an event endpoint is an ISO date. */
    private boolean isDate(String value, LocalDate date) {
        try {
            return LocalDate.parse(value).equals(date);
        } catch (DateTimeParseException exception) {
            return false;
        }
    }

    /** Returns this event in Murphy's save-file format. */
    @Override
    public String toDataString() {
        return "E | " + (isDone() ? "1" : "0") + " | " + escapeDataField(description)
                + " | " + escapeDataField(from) + " | " + escapeDataField(to);
    }

    /** Returns the display text for this event. */
    @Override
    public String toString() {
        return "[E][" + getStatusIcon() + "] " + description
                + " (from: " + from + " to: " + to + ")";
    }
}
