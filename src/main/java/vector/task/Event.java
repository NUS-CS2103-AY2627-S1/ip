package vector.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import vector.VectorException;
import vector.parser.DateTimeParser;

/**
 * Represents an Event task.
 */
public class Event extends Task {

    protected LocalDateTime fromTime;
    protected LocalDateTime toTime;

    /**
     * Constructs an Event task.
     *
     * @param description The description of the event.
     * @param from The start date/time of the event.
     * @param to The end date/time of the event.
     * @throws VectorException if the date formats are invalid.
     */
    public Event(String description, String from, String to) throws vector.VectorException {
        super(description);
        this.fromTime = DateTimeParser.parse(from);
        this.toTime = DateTimeParser.parse(to);
        if (!this.fromTime.isBefore(this.toTime)) {
            throw new vector.VectorException("Event start time must be before end time.");
        }
    }

    @Override
    public boolean occursOn(LocalDate date) {
        LocalDate startDate = fromTime.toLocalDate();
        LocalDate endDate = toTime.toLocalDate();
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + DateTimeParser.format(fromTime)
                + " to: " + DateTimeParser.format(toTime) + ")";
    }

    @Override
    public String toFileFormat() {
        return "E" + super.toFileFormat() + " | " + DateTimeParser.formatForFile(fromTime)
                + " | " + DateTimeParser.formatForFile(toTime);
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        Event event = (Event) obj;
        return fromTime.equals(event.fromTime) && toTime.equals(event.toTime);
    }
}

