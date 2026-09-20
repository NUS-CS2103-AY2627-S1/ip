package duchess.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Represents an event with start/end dates, or a preserved legacy time description. */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    /** The original time text for legacy events; null for events with date ranges. */
    private final String at;
    private final LocalDate from;
    private final LocalDate to;

    /**
     * Creates an event in the legacy free-text format without inventing missing dates.
     *
     * @param description the event description.
     * @param at the original event time text.
     */
    public Event(String description, String at) {
        super(description, TaskType.EVENT);
        assert at != null && !at.isBlank() : "An event must have a non-blank time value";
        this.at = at;
        from = null;
        to = null;
    }

    /**
     * Creates an event whose end date is later than its start date.
     *
     * @param description the event description.
     * @param from the start date in yyyy-MM-dd format.
     * @param to the end date in yyyy-MM-dd format.
     * @throws IllegalArgumentException if the end is not later than the start.
     * @throws java.time.format.DateTimeParseException if either date is invalid.
     */
    public Event(String description, String from, String to) {
        super(description, TaskType.EVENT);
        this.from = LocalDate.parse(from);
        this.to = LocalDate.parse(to);
        if (!this.to.isAfter(this.from)) {
            throw new IllegalArgumentException("Event end must be later than start");
        }
        at = null;
    }

    /** Returns the legacy event text, or null for a dated event. */
    public String getAt() {
        return at;
    }

    /** Returns the start date, or null for a legacy event. */
    public LocalDate getFrom() {
        return from;
    }

    /** Returns the end date, or null for a legacy event. */
    public LocalDate getTo() {
        return to;
    }

    /** Returns whether this event has structured start and end dates. */
    public boolean hasDateRange() {
        return from != null;
    }

    /** Returns the event's type, status, and formatted dates or legacy time. */
    @Override
    public String toString() {
        if (!hasDateRange()) {
            return super.toString() + " (at: " + at + ")";
        }
        return super.toString() + " (from: " + from.format(DISPLAY_DATE_FORMAT)
                + " to: " + to.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
