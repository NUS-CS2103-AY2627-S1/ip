package discitrack.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that happens over a date range.
 */
public class Event extends Task {
    private final LocalDate from;
    private final LocalDate to;

    /**
     * Creates an event task with the given activity and date range.
     *
     * @param activity the activity represented by the event.
     * @param from the start date of the event.
     * @param to the end date of the event.
     */
    public Event(String activity, LocalDate from, LocalDate to) {
        super(activity);
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("UHOH! The end date cannot be before the start date.");
        }
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the start date of this event.
     *
     * @return the start date.
     */
    public LocalDate getFrom() {
        return this.from;
    }

    /**
     * Returns the end date of this event.
     *
     * @return the end date.
     */
    public LocalDate getTo() {
        return this.to;
    }

    /**
     * Returns the display text for this event task.
     *
     * @return the event task formatted for display.
     */
    @Override
    public String toString() {
        String stringFrom = DateTimeFormatter.ofPattern("MMM dd yyyy").format(from);
        String stringTo = DateTimeFormatter.ofPattern("MMM dd yyyy").format(to);
        return String.format("[E] %s (from: %s to: %s)", formatTaskBody(), stringFrom, stringTo) + formatTags();
    }
}
