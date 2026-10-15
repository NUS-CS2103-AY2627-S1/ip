package trackie.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that occurs between a start and end date or time.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    private final LocalDate from;
    private final LocalDate to;

    /**
     * Creates an incomplete event with the given description and timing.
     *
     * @param description description of the event
     * @param from date when the event starts
     * @param to date when the event ends
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description, TaskType.EVENT);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event with its type, status, and timing.
     *
     * @return formatted event
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + this.from.format(DISPLAY_FORMAT)
                + " to: " + this.to.format(DISPLAY_FORMAT) + ")";
    }

    /**
     * Returns a data-file line containing this event's start and end times.
     *
     * @return serialized event
     */
    @Override
    public String toDataString() {
        return super.toDataString() + " | " + this.from + " | " + this.to;
    }
}
