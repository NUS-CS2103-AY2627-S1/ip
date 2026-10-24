package nabmak.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents an Event object with user specified parameters.
 */
public class Event extends Task {
    private static final DateTimeFormatter DATE_FORMAT =
        DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm");
    private LocalDateTime end;
    private LocalDateTime start;

    /**
     * Creates an event based on user input.
     *
     * @param desc description of task
     * @param start start date
     * @param end end date
     */
    public Event(String desc, LocalDateTime start, LocalDateTime end) {
        super(desc);
        this.start = start;
        this.end = end;
    }
    /**
     * Returns start date and time of task.
     *
     * @return start date and time
     */
    public LocalDateTime getStart() {
        return this.start;
    }

    /**
     * Returns end date and time of task.
     *
     * @return end date and time
     */
    public LocalDateTime getEnd() {
        return this.end;
    }

    /**
     * Converts task into a string format with its description, start date and end date.
     *
     * @return event task in string format
     */
    @Override
    public String toString() {
        return "[Event]" + super.toString() + " (from: "
            + start.format(DATE_FORMAT) + " to: "
            + end.format(DATE_FORMAT) + ")";
    }
}
