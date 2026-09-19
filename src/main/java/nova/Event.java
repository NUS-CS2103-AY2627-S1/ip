package nova;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that happens between a start date and an end date.
 */
public class Event extends Task {
    private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    private final LocalDate from;
    private final LocalDate to;

    /**
     * Creates an event with the given description, start date and end date.
     *
     * @param description the event description
     * @param from the start date
     * @param to the end date
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the start date of this event.
     *
     * @return the start date
     */
    public LocalDate getFrom() {
        return this.from;
    }

    /**
     * Returns the end date of this event.
     *
     * @return the end date
     */
    public LocalDate getTo() {
        return this.to;
    }

    /**
     * Returns a display string of this event, e.g.
     * "[E][ ] project meeting (from: Aug 28 2026 to: Aug 29 2026)".
     *
     * @return the display string
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + this.from.format(OUTPUT_FORMAT)
                + " to: " + this.to.format(OUTPUT_FORMAT) + ")";
    }
}
