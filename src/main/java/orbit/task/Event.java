package orbit.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that takes place between two dates.
 */
public class Event extends Task {
    /** Message used when an event does not have a meaningful date range. */
    public static final String INVALID_DATE_RANGE_MESSAGE =
            "The end date of an event must be after its start date.";

    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    private final LocalDate from;
    private final LocalDate to;

    /**
     * Creates an incomplete event.
     *
     * @param description description shown to the user
     * @param from start date
     * @param to end date
     */
    public Event(String description, LocalDate from, LocalDate to) {
        this(description, from, to, false);
    }

    /**
     * Creates an event with a stored completion status.
     *
     * @param description description shown to the user
     * @param from start date
     * @param to end date
     * @param isDone whether the event is completed
     */
    public Event(String description, LocalDate from, LocalDate to, boolean isDone) {
        super(description, isDone);
        if (!from.isBefore(to)) {
            throw new IllegalArgumentException(INVALID_DATE_RANGE_MESSAGE);
        }
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event start date.
     *
     * @return start date
     */
    public LocalDate getFrom() {
        return from;
    }

    /**
     * Returns the event end date.
     *
     * @return end date
     */
    public LocalDate getTo() {
        return to;
    }

    /**
     * Formats this event using Orbit's user-facing date format.
     *
     * @return event marker, completion status, description, and date range
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from.format(DISPLAY_FORMAT)
                + " to: " + to.format(DISPLAY_FORMAT) + ")";
    }
}
