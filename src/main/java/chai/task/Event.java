package chai.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** A task that starts and ends on specified dates. */
public class Event extends Task {
    /** The human-friendly format used when displaying event dates. */
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    /** The date on which this event starts. */
    protected LocalDate from;

    /** The date on which this event ends. */
    protected LocalDate to;

    /**
     * Creates an incomplete event.
     *
     * @param description Text describing the event.
     * @param from Date on which the event starts.
     * @param to Date on which the event ends.
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        assert from != null : "Event start date must not be null";
        assert to != null : "Event end date must not be null";
        assert !to.isBefore(from) : "Event end date must not precede its start date";
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event's start date.
     *
     * @return Event start date.
     */
    public LocalDate getFrom() {
        return from;
    }

    /**
     * Returns the event's end date.
     *
     * @return Event end date.
     */
    public LocalDate getTo() {
        return to;
    }

    @Override
    public LocalDate getSortDate() {
        return from;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from.format(DISPLAY_FORMAT)
                + " to: " + to.format(DISPLAY_FORMAT) + ")";
    }
}
