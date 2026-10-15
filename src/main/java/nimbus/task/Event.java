package nimbus.task;

import java.time.LocalDate;

/**
 * Represents an Event task in the Nimbus application.
 */
public class Event extends Task {

    private final LocalDate from;
    private final LocalDate to;

    /**
     * Constructs an Event object, with a name, from date and end date.
     *
     * @param name The name of the event.
     * @param from The date the event starts.
     * @param to   The date the event ends.
     */
    public Event(String name, LocalDate from, LocalDate to) {
        super(name, TaskType.EVENT);
        this.from = from;
        this.to = to;
    }

    public String getFrom() {
        return from.format(OUTPUT_FORMAT);
    }

    public String getTo() {
        return to.format(OUTPUT_FORMAT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toFileString() {
        return super.toFileString() + "|" + from.toString() + "|" + to.toString();
    }

    @Override
    public String toString() {
        return super.toString() + " (from: " + getFrom() + " to: " + getTo() + ")";
    }
}
