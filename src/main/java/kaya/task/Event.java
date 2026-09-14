package kaya.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that occurs between specified start and end times.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private final LocalDate from;
    private final LocalDate to;

    /**
     * Creates an event with the given description, start, and end.
     *
     * @param description the description of the event
     * @param from the event's starting date
     * @param to the event's ending date
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event's starting date or time for saving to disk.
     *
     * @return the event's starting date
     */
    public LocalDate getFrom() {
        return from;
    }

    /**
     * Returns the event's ending date or time for saving to disk.
     *
     * @return the event's ending date
     */
    public LocalDate getTo() {
        return to;
    }

    /** {@inheritDoc} */
    @Override
    public Task copy() {
        return copyStatusTo(new Event(getDescription(), from, to));
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from.format(DISPLAY_FORMAT)
                + " to: " + to.format(DISPLAY_FORMAT) + ")";
    }
}
