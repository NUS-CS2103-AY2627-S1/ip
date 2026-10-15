package lumi.task;

import java.time.Duration;
import java.time.LocalDateTime;

import lumi.datetime.DateTimeParser;

/**
 * Represents a task that occurs between a start and an end date or time.
 */
public class Event extends Task {
    /** The date or time at which the event starts. */
    private LocalDateTime from;

    /** The date or time at which the event ends. */
    private LocalDateTime to;

    /**
     * Creates an incomplete event task.
     *
     * @param description Description of the event.
     * @param from Date or time at which the event starts.
     * @param to Date or time at which the event ends.
     * @throws IllegalArgumentException If either date is missing or the event does not end after it starts.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        if (from == null || to == null) {
            throw new IllegalArgumentException("Event dates cannot be null");
        }
        if (!to.isAfter(from)) {
            throw new IllegalArgumentException("An event must end after it starts");
        }
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event's start details for storage and other non-UI uses.
     *
     * @return The date or time at which the event starts.
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns the event's end details for storage and other non-UI uses.
     *
     * @return The date or time at which the event ends.
     */
    public LocalDateTime getTo() {
        return to;
    }

    /**
     * Moves this event to a new start date or time while preserving its duration.
     *
     * @param newStartDate New date or time at which the event starts.
     */
    public void reschedule(LocalDateTime newStartDate) {
        Duration duration = Duration.between(from, to);
        from = newStartDate;
        to = newStartDate.plus(duration);
    }

    /**
     * Returns the event in the format used when displaying task lists.
     *
     * @return The task type, status, description, start, and end.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + DateTimeParser.format(from)
                + " to: " + DateTimeParser.format(to) + ")";
    }
}
