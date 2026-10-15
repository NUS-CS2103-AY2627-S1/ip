package apollo.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** A task that takes place between two dates and times. */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd uuuu, h:mma");

    protected LocalDateTime from;
    protected LocalDateTime to;

    /**
     * Creates an event spanning the supplied start and end times.
     *
     * @param event description of the event
     * @param from date and time when the event starts
     * @param to date and time when the event ends
     */
    public Event(String event, LocalDateTime from, LocalDateTime to) {
        assert !to.isBefore(from)
                : "Event end time must not be before its start time";
        this.from = from;
        this.to = to;
        super(event);
    }

    @Override
    public String getIcon() {
        return "E";
    }

    /** Returns the date and time when this event starts. */
    public LocalDateTime getFrom() {
        return this.from;
    }

    /** Returns the date and time when this event ends. */
    public LocalDateTime getTo() {
        return this.to;
    }

    @Override
    public String toString() {
        return String.format("[%s] [%s] %s (from: %s to: %s)", this.getIcon(), this.getStatusIcon(),
                this.description, this.from.format(DISPLAY_FORMATTER), this.to.format(DISPLAY_FORMATTER));
    }

    @Override
    public String toFileString() {
        return String.format("E | %d | %s | %s | %s", this.isDone ? 1 : 0, this.description,
                this.from.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                this.to.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}
