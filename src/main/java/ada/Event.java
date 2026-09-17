package ada;

import java.time.LocalDateTime;

/**
 * Represents an event‑type task with a description, start datetime and end datetime.
 * Subclass of {@code Task}. Supports formatted display and file persistence
 * for event start‑and‑end time information.
 */
public class Event extends Task {

    /**
     * Default fallback time used when input only provides a date without time.
     */
    private final LocalDateTime start;
    private final LocalDateTime end;

    /**
     * Constructs an Event task with description, start and end date‑time.
     *
     * @param description text content describing the event
     * @param start starting date‑time of this event
     * @param end ending date‑time of this event
     */
    public Event(String description, LocalDateTime start, LocalDateTime end) {
        super(description);
        assert start != null && end != null : "An event must have both a start and an end date-time";
        this.start = start;
        this.end = end;
    }

    /**
     * Returns the event start date and time.
     *
     * @return event start date and time
     */
    public LocalDateTime getStart() {
        return start;
    }

    private String formatDateTime(LocalDateTime dt) {
        StringBuilder sb = new StringBuilder();
        sb.append(dt.toLocalDate()
                .format(DateTimeFormats.DISPLAY_DATE));
        if (!dt.toLocalTime()
                .equals(DateTimeFormats.DEFAULT_TIME)) {
            sb.append(" ")
                    .append(dt.format(DateTimeFormats.DISPLAY_TIME));
        }
        return sb.toString();
    }

    /**
     * Returns console‑friendly string representation for this event.
     * Prefixes "[E]" and shows formatted start‑to‑end time range.
     *
     * @return formatted string for UI output
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + formatDateTime(this.start)
                + " to: " + formatDateTime(this.end) + ")";
    }

    /**
     * Produces storage‑compatible line for saving event to file.
     * Uses "E | " prefix and stores raw ISO datetime values.
     *
     * @return formatted text line for persistent file storage
     */
    @Override
    public String toFileFormat() {
        return "E | " + super.toFileFormat() + " | " + this.start
                + " | " + this.end;
    }
}
