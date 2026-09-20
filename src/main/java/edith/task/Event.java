package edith.task;

import java.time.LocalDateTime;

/**
 * Represents a task with a start time and an end time.
 */
public class Event extends Task {
    /** The start time or date text supplied by the user. */
    protected String from;

    /** The end time or date text supplied by the user. */
    protected String to;

    /** Parsed event start, when the input uses a supported date format. */
    protected LocalDateTime fromDateTime;

    /** Parsed event end, when the input uses a supported date format. */
    protected LocalDateTime toDateTime;

    /**
     * Creates an event task.
     *
     * @param description the event description
     * @param from the event start text
     * @param to the event end text
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
        this.fromDateTime = DateTimeParser.parse(from);
        this.toDateTime = DateTimeParser.parse(to);
    }

    @Override
    public LocalDateTime getDateTimeForSorting() {
        return fromDateTime == null ? DateTimeParser.parseForSorting(from) : fromDateTime;
    }

    @Override
    public boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other)
                && normalize(from).equals(normalize(((Event) other).from))
                && normalize(to).equals(normalize(((Event) other).to));
    }

    @Override
    protected String getTypeCode() {
        return "E";
    }

    @Override
    public String toFileFormat() {
        return super.toFileFormat() + " | " + (fromDateTime == null ? from : fromDateTime)
                + " | " + (toDateTime == null ? to : toDateTime);
    }

    /**
     * Returns this task with its type, status, start, and end time.
     *
     * @return the formatted event task
     */
    @Override
    public String toString() {
        String displayFrom = fromDateTime == null ? from : DateTimeParser.format(fromDateTime);
        String displayTo = toDateTime == null ? to : DateTimeParser.format(toDateTime);
        return "[E]" + super.toString() + " (from: " + displayFrom + " to: " + displayTo + ")";
    }
}
