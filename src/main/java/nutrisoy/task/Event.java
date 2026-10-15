package nutrisoy.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task occurring over a date range.
 */
public class Event extends Task {
    protected LocalDate from;
    protected LocalDate to;

    /**
     * Creates an event task with its description and date range.
     *
     * @param description description of the task
     * @param from start date of the event
     * @param to end date of the event
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        assert from != null && to != null : "Event dates must not be null";
        this.from = from;
        this.to = to;
    }

    /**
     * Returns this event in the storage-file format.
     *
     * @return storage-file representation of this event
     */
    @Override
    public String toFileFormat() {
        return "E | " + getCoreFileFormat() + " | " + from + " | " + to + getTagsFileSuffix();
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other)
                && from.equals(((Event) other).from)
                && to.equals(((Event) other).to);
    }

    /**
     * Returns a display representation of this event.
     *
     * @return display representation of this event
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: "
                + from.format(DateTimeFormatter.ofPattern("MMM dd yyyy"))
                + " to: " + to.format(DateTimeFormatter.ofPattern("MMM dd yyyy")) + ")";
    }
}
