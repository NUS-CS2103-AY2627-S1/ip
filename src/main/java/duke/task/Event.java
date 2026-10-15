package duke.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that starts and ends at specific dates or times.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private final LocalDate from;
    private final LocalDate to;

    /**
     * Creates a new event task.
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        assert from != null : "Event start date should not be null";
        assert to != null : "Event end date should not be null";
        this.from = from;
        this.to = to;
    }

    /**
     * Returns when this event starts.
     */
    public LocalDate getFrom() {
        return from;
    }

    /**
     * Returns when this event ends.
     */
    public LocalDate getTo() {
        return to;
    }

    /**
     * Returns this event in the format shown to the user.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: "
                + from.format(DISPLAY_DATE_FORMAT) + " to: " + to.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
