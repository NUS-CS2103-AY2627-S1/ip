package peter.task;

import java.time.DateTimeException;
import java.time.LocalTime;

import peter.exception.PeterException;

/**
 * Represents an Event type of Task.
 * An event has start and end times in addition to the basic task information.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Constructs an Event object with the specified description and times.
     * The task is initially not completed.
     *
     * @param description The textual description of the task.
     * @param from The start time of the event in HHmm format.
     * @param to The end time of the event in HHmm format.
     * @throws PeterException If either time is invalid or the range is not chronological.
     */
    public Event(String description, String from, String to) throws PeterException {
        super(description);
        LocalTime startTime = parseTime(from);
        LocalTime endTime = parseTime(to);
        if (!startTime.isBefore(endTime)) {
            throw new PeterException("An event must end after it starts. Please chart a later /to time.");
        }
        this.from = from;
        this.to = to;
    }

    private static LocalTime parseTime(String value) throws PeterException {
        if (value == null || !value.matches("\\d{4}")) {
            throw new PeterException("Event times must use 24-hour HHmm format, such as 0930 or 1600.");
        }
        try {
            int hour = Integer.parseInt(value.substring(0, 2));
            int minute = Integer.parseInt(value.substring(2));
            return LocalTime.of(hour, minute);
        } catch (DateTimeException e) {
            throw new PeterException("That event time doesn't exist. Use a valid 24-hour time such as 0930.");
        }
    }

    @Override
    public boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other)
                && from.equals(((Event) other).from)
                && to.equals(((Event) other).to);
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }

    @Override
    public String toFileFormat() {
        return "E | " + super.toFileFormat() + " | " + this.from + " | " + this.to;
    }
}
