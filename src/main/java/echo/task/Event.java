package echo.task;

import java.time.LocalDateTime;
import java.util.Objects;

import echo.utils.DateTimeUtility;


/**
 * Represents a task that starts and ends at specific date-times.
 */
public class Event extends Task {
    private static final TaskType TYPE = TaskType.EVENT;
    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Creates an incomplete event with the given description and schedule.
     *
     * @param description text that describes the event
     * @param from date-time at which the event starts
     * @param to date-time at which the event ends
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = Objects.requireNonNull(from, "An event start date-time must not be null.");
        this.to = Objects.requireNonNull(to, "An event end date-time must not be null.");
        if (!from.isBefore(to)) {
            throw new IllegalArgumentException("An event must start before it ends.");
        }
    }

    /**
     * Creates {@link String} representation of the {@link Event} instance
     * @return String representation of event
     */
    @Override
    public String toString() {
        return "[" + TYPE.toString() + "]" + super.toString()
                + " (from: " + this.from.format(DateTimeUtility.DISPLAY_FORMAT)
                + " to: " + this.to.format(DateTimeUtility.DISPLAY_FORMAT) + ")";
    }

    /**
     * Creates save {@link String} representation of the {@link Event} instance
     * @return Save string representation of event
     */
    @Override
    public String toSaveFormat() {
        return TYPE.toString() + " | " + super.toSaveFormat() + " | " + this.from + " | " + this.to;
    }

    /**
     * Reconstructs an event from its save-format fields (type letter,
     * completion date-time, description, start time, end time).
     *
     * @param fields the split save-file line for this event
     * @return the reconstructed event
     */
    public static Event fromSaveFormat(String[] fields) {
        if (fields.length != 5 || fields[3].isEmpty() || fields[4].isEmpty()) {
            throw new IllegalArgumentException("A saved event must have exactly 5 non-empty fields.");
        }
        return new Event(fields[2], LocalDateTime.parse(fields[3]), LocalDateTime.parse(fields[4]));
    }

    @Override
    public TaskType getTaskType() {
        return TYPE;
    }

    @Override
    boolean hasSameSchedule(Task other) {
        Event otherEvent = (Event) other;
        return from.equals(otherEvent.from) && to.equals(otherEvent.to);
    }
}
