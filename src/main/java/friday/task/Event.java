package friday.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A task scheduled between a start and an end time.
 */
public class Event extends Task {
    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Parses both endpoints and rejects a backwards interval; equal endpoints are allowed.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = TaskDateTime.parse(from);
        this.to = TaskDateTime.parse(to);
        if (this.to.isBefore(this.from)) {
            throw new IllegalArgumentException("An event cannot end before it starts.");
        }
        assert !this.to.isBefore(this.from)
                : "A constructed event must not end before it starts.";
    }

    /**
     * Returns the validated event start date and time.
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns the validated event end date and time.
     */
    public LocalDateTime getTo() {
        return to;
    }

    /**
     * Matches every calendar date from the start date through the end date, inclusive.
     *
     * @return {@code true} if the supplied date falls within the event's calendar dates.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(from.toLocalDate()) && !date.isAfter(to.toLocalDate());
    }

    /**
     * Returns the event type, completion marker, description, and formatted endpoints.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + TaskDateTime.format(from)
                + " to: " + TaskDateTime.format(to) + ")";
    }
}
