package gatsby.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** A task that takes place during a specified date-time range. */
public class Event extends Task {
    /** The date and time at which the event starts. */
    private final LocalDateTime start;

    /** The date and time at which the event ends. */
    private final LocalDateTime end;

    /**
     * Creates an event task.
     *
     * @param description the task description
     * @param start the event start date and time
     * @param end the event end date and time
     */
    public Event(String description, LocalDateTime start, LocalDateTime end) {
        super(description);
        if (start == null || end == null) {
            throw new IllegalArgumentException("An event must have both start and end date-times.");
        }
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("An event must end after it starts.");
        }
        this.start = start;
        this.end = end;
    }

    /**
     * Returns the date and time at which this event starts.
     *
     * @return the event start date and time
     */
    public LocalDateTime getStart() {
        return start;
    }

    /**
     * Returns the date and time at which this event ends.
     *
     * @return the event end date and time
     */
    public LocalDateTime getEnd() {
        return end;
    }

    /**
     * Returns whether another task has the same event details as this task.
     *
     * @param other the task to compare with
     * @return true when both events have the same details
     */
    @Override
    public boolean hasSameDetails(Task other) {
        return other instanceof Event
                && super.hasSameDetails(other)
                && start.equals(((Event) other).start)
                && end.equals(((Event) other).end);
    }

    /**
     * Returns the event with its date-time range formatted for console display.
     *
     * @return the human-readable event representation
     */
    @Override
    public String toString() {
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm:ss");
        String formattedStart = this.start.format(outputFormatter);
        String formattedEnd = this.end.format(outputFormatter);
        return "[E]" + super.toString() + " (from: " + formattedStart + " to: " + formattedEnd + ")";
    }

    /**
     * Returns the event in the pipe-separated format used by storage.
     *
     * @return the saved event representation
     */
    @Override
    public String toFileFormat() {
        return "E | " + super.toFileFormat() + " | " + this.start + " | " + this.end;
    }
}
