package bubba;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task with a start and end date and time.
 */
public class Event extends Task {
    /** Start date and time of the event. */
    protected LocalDateTime from;
    /** End date and time of the event. */
    protected LocalDateTime to;

    /**
     * Creates an incomplete event using the supplied times without validating their order.
     *
     * @param description Description of the event.
     * @param from Start date and time.
     * @param to End date and time.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description, TaskType.EVENT);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event summary with both times formatted as {@code MMM dd yyyy HH:mm}.
     *
     * @return Human-readable event summary.
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm");
        return super.toString()
                + " (from: " + from.format(formatter)
                + " to: " + to.format(formatter) + ")";
    }

    /**
     * Returns the event record with both times formatted as {@code dd/MM/yyyy HHmm}.
     *
     * @return Pipe-separated event record.
     */
    @Override
    public String toStorageString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HHmm");
        return super.toStorageString()
                + " | " + from.format(formatter)
                + " | " + to.format(formatter);
    }
}
