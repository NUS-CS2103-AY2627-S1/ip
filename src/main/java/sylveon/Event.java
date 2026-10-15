package sylveon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** Represents a task that takes place during a specified date range. */
public class Event extends Task {
    private final LocalDate from;
    private final LocalDate to;

    /** Creates an event task. */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /** Returns the starting date of the event. */
    public LocalDate getFrom() {
        return from;
    }

    /** Returns the ending date of the event. */
    public LocalDate getTo() {
        return to;
    }

    /** Returns the display representation of this event task. */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d yyyy");
        return "[E]" + super.toString()
                    + " (from: " + from.format(formatter) + " to: " + to.format(formatter) + ")";
    }
}
