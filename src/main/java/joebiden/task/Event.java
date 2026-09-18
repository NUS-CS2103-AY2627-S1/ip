package joebiden.task;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/**
 * Represents an event task with a start and end date and time.
 */
public class Event extends Task {

    private final LocalDateTime from;
    private final LocalDateTime to;
    /**
     * Creates an event task with the given description, start time, and end time.
     *
     * @param name Description of the event.
     * @param from Start date and time of the event.
     * @param to End date and time of the event.
     */
    public Event(String name, LocalDateTime from, LocalDateTime to) {
        super(name);
        this.from = from;
        this.to = to;
    }
    /**
     * Returns the start date and time of the event.
     *
     * @return Start date and time.
     */
    public LocalDateTime getFrom() {
        return from;
    }
    /**
     * Returns the end date and time of the event.
     *
     * @return End date and time.
     */
    public LocalDateTime getTo() {
        return to;
    }
    /**
     * Returns a formatted string representation of the event.
     *
     * @return Formatted event string.
     */
    @Override
    public String toString() {
        DateTimeFormatter outputFormat =
                DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a");

        return "[E]" + super.toString()
                + " (from: " + from.format(outputFormat)
                + " to: " + to.format(outputFormat) + ")";
    }
}
