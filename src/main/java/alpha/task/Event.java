package alpha.task;

import java.time.LocalDateTime;

/** A task that takes place between a specified start and end time. */
public class Event extends Task {
    private final LocalDateTime from;
    private final LocalDateTime to;

    /** Creates an event by parsing its start and end date/time text. */
    public Event(String description, String from, String to) {
        this(description, DateTimeParser.parse(from), DateTimeParser.parse(to));
    }

    /** Creates an event with already parsed start and end dates and times. */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        assert from != null : "An event must have a parsed start time";
        assert to != null : "An event must have a parsed end time";
        this.from = from;
        this.to = to;
    }

    /** Returns the start time of this event. */
    public LocalDateTime getFrom() {
        return this.from;
    }

    /** Returns the end time of this event. */
    public LocalDateTime getTo() {
        return this.to;
    }

    @Override
    protected String getTypeIcon() {
        return "E";
    }

    @Override
    protected String getTimeDetails() {
        return String.format(" (from: %s to: %s)", DateTimeParser.format(this.from),
                DateTimeParser.format(this.to));
    }

    @Override
    protected LocalDateTime getSortDateTime() {
        return this.from;
    }
}
