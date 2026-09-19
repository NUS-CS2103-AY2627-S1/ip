package carl.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import carl.util.DateParser;

/**
 * Represents an event task with a start date/time and an end date/time.
 */
public class Event extends Task {

    /** Date and time when the event starts. */
    protected final LocalDateTime from;

    /** Date and time when the event ends. */
    protected final LocalDateTime to;

    /**
     * Constructs a new Event task with the specified item, start time, and end time.
     * The task status is initialized to NOT_DONE by default.
     *
     * @param name The description or name of the event.
     * @param from The starting date and time of the event.
     * @param to   The ending date and time of the event.
     */
    public Event(String name, LocalDateTime from, LocalDateTime to) {
        super(name, TaskType.EVENT);
        this.from = from;
        this.to = to;
    }

    /**
     * Constructs an Event task with a specified completion status.
     * This is typically used when loading tasks from a saved file.
     *
     * @param name   The description or name of the event.
     * @param status The completion status of the event.
     * @param from   The starting date and time of the event.
     * @param to     The ending date and time of the event.
     */
    public Event(String name, TaskStatus status, LocalDateTime from, LocalDateTime to) {
        super(name, status, TaskType.EVENT);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from.format(DateParser.getDisplayFormatter())
                + " to: " + to.format(DateParser.getDisplayFormatter()) + ")";
    }

    @Override
    public String toSaveFormat() {
        return String.format("%s | %s | %s", super.toSaveFormat(), from.format(DateParser.getStorageFormatter()),
                to.format(DateParser.getStorageFormatter()));
    }

    @Override
    protected String getIdentity() {
        return super.getIdentity() + "|" + from + "|" + to;
    }

    @Override
    protected boolean isOnDate(LocalDate targetDate) {
        return !targetDate.isBefore(this.from.toLocalDate())
                && !targetDate.isAfter(this.to.toLocalDate());
    }
}
