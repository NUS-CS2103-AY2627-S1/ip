package anaconda.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that occurs between specified start and end times.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private final LocalDate from;
    private final LocalDate to;

    /**
     * Creates an Event with its description, start date, and end date.
     *
     * @param description Description of the task.
     * @param from Start date.
     * @param to End date.
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event's start date.
     *
     * @return Start date.
     */
    public LocalDate getFrom() {
        return from;
    }

    /**
     * Returns the event's end date.
     *
     * @return End date.
     */
    public LocalDate getTo() {
        return to;
    }

    @Override
    public LocalDate getEndDate() {
        return to;
    }

    /**
     * Formats the event using its type, completion status, description, and date range.
     *
     * @return Display representation of this event.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from.format(DISPLAY_FORMATTER)
                + " to: " + to.format(DISPLAY_FORMATTER) + ")";
    }
}
