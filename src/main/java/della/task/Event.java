package della.task;

import java.time.LocalDateTime;

import della.util.DateParser;

/**
 * Represents a task that occurs within a specified time range.
 */
public class Event extends Task {
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;

    /**
     * Creates an incomplete event task with the specified name and time range.
     *
     * @param name Description of the event task.
     * @param from Start date and time of the event.
     * @param to End date and time of the event.
     */
    public Event(String name, LocalDateTime from, LocalDateTime to) {
        super(name);
        assert from != null && to != null : "Event timestamps must not be null";
        assert !to.isBefore(from) : "Event end timestamp must not precede its start timestamp";
        this.startDateTime = from;
        this.endDateTime = to;
    }

    /**
     * Creates an event task with the specified name, completion status, and time range.
     *
     * @param name Description of the event task.
     * @param isDone Completion status of the event task.
     * @param from Start date and time of the event.
     * @param to End date and time of the event.
     */
    public Event(String name, boolean isDone, LocalDateTime from, LocalDateTime to) {
        super(name);
        assert from != null && to != null : "Event timestamps must not be null";
        assert !to.isBefore(from) : "Event end timestamp must not precede its start timestamp";
        this.isDone = isDone;
        this.startDateTime = from;
        this.endDateTime = to;
    }

    @Override
    public String formatForStorage() {
        return String.format(
                "%s|%s|%s|%s|%s", "E", super.isDone ? "1" : "0",
                this.name, DateParser.printDateTime(this.startDateTime), DateParser.printDateTime(this.endDateTime));
    }

    @Override
    public String toString() {
        return String.format(
                "[E]%s (from: %s to: %s)",
                super.toString(), DateParser.printDateTime(this.startDateTime),
                DateParser.printDateTime(this.endDateTime));
    }
}
