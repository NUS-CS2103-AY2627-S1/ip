package tuesday.task;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Represents a task with a start and end date-time.
 */
public class Event extends Task {
    private final DateTimeValue start;
    private final DateTimeValue end;

    /**
     * Creates an event task.
     *
     * @param name the task description.
     * @param start the event start date-time.
     * @param end the event end date-time.
     */
    public Event(String name, DateTimeValue start, DateTimeValue end) {
        super(name);
        this.start = start;
        this.end = end;
    }

    /**
     * Returns the task in its saved display format.
     *
     * @return the formatted event task.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: "
                + DateTimeFormat.format(start.date(), start.time())
                + " to: " + DateTimeFormat.format(end.date(), end.time()) + ")";
    }

    /**
     * Stores the optional date and time components of one event boundary.
     */
    public record DateTimeValue(LocalDate date, LocalTime time) {
    }
}
