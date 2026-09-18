package hamtaro.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** A task that takes place over a specified time range. */
public class Event extends Task{
    private LocalDateTime from;
    private LocalDateTime to;

    /** Creates an incomplete event with a start and end time. */
    public Event(String description, LocalDateTime from, LocalDateTime to){
        super(description);
        // Event times are parsed before construction and are required for display.
        assert from != null && to != null : "An event must have start and end times";
        this.from = from;
        this.to = to;
    }

    /** Creates an event with its completion status restored from storage. */
    public Event(String description, LocalDateTime from, LocalDateTime to, boolean isDone) {
        super(description, isDone);
        // Loaded records are expected to contain both parseable event times.
        assert from != null && to != null : "An event must have start and end times";
        this.from = from;
        this.to = to;
    }

    /** Returns the event start text for persistent storage. */
    public LocalDateTime getFrom() {
        return from;
    }

    /** Returns the event end text for persistent storage. */
    public LocalDateTime getTo() {
        return to;
    }

    /** Returns a displayable representation of this event. */
    @Override
    public String toString(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd yyyy HHmm");
        return "[E]" + super.toString() + " (from: " + this.from.format(dtf) + " to: " + this.to.format(dtf) + ")";
    }
}
