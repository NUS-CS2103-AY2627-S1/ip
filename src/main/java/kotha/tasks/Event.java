package kotha.tasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Represents a task that takes place over a time range. */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu hh:mma");
    protected LocalDateTime from;
    protected LocalDateTime to;

    /** Creates an incomplete event with the supplied description and time range. */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /** Returns the event start time for persistent storage. */
    public LocalDateTime getFrom() {
        return from;
    }

    /** Returns the event end time for persistent storage. */
    public LocalDateTime getTo() {
        return to;
    }

    @Override

    public String toString() {
        return "[E]" + super.toString() + " (from: " + from.format(DISPLAY_FORMAT)
                + " to: " + to.format(DISPLAY_FORMAT) + ")";
    }

    @Override
    public void printAddText() {
        System.out.println("Master I have added the following event:");
        System.out.println(this.toString());
    }
}
