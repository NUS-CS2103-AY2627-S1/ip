package yapper;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a task that takes place between specified start and end times.
 */
public class Event extends Task {
    /** Inclusive start date and time. */
    private final LocalDateTime from;
    /** Inclusive end date and time. */
    private final LocalDateTime to;

    /**
     * Creates an incomplete event.
     *
     * @param description Description of the event.
     * @param from Start date and time of the event.
     * @param to End date and time of the event.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns this event as a pipe-separated storage record.
     *
     * @return Task type, completion state, description, start date and time, and end date and time.
     */
    @Override
    public String toDataString() {
        return "E | " + (isDone ? "1" : "0") + " | " + description
                + " | " + TaskDateTime.formatForStorage(from)
                + " | " + TaskDateTime.formatForStorage(to);
    }

    /**
     * Returns whether the date falls between the event start and end dates, inclusive.
     *
     * @param date Calendar date to check.
     * @return Whether the date matches, ignoring time of day.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        LocalDate startDate = from.toLocalDate();
        LocalDate endDate = to.toLocalDate();
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Returns this event in the format used by the chatbot.
     *
     * @return Type marker, completion status, and description with its scheduled date or times.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + TaskDateTime.formatForDisplay(from)
                + " to: " + TaskDateTime.formatForDisplay(to) + ")";
    }
}
