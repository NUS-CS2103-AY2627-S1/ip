package bob.task;

import java.time.LocalDateTime;

import bob.parser.DateTimeParser;

/**
 * Represents a task that takes place over a date or time range.
 */
public class Event extends Task {
    private final TaskDateTime from;
    private final TaskDateTime to;

    /**
     * Creates an event task.
     *
     * @param description description of the event.
     * @param from start date and optional time.
     * @param to end date and optional time.
     */
    public Event(String description, TaskDateTime from, TaskDateTime to) {
        super(description, TaskType.EVENT);
        if (from == null || to == null) {
            throw new IllegalArgumentException("An event needs both a start and an end date-time.");
        }
        if (!toLocalDateTime(from).isBefore(toLocalDateTime(to))) {
            throw new IllegalArgumentException("An event's end time must be later than its start time.");
        }
        this.from = from;
        this.to = to;
    }

    /**
     * Converts a task date-time to a comparable value, treating a date-only value as midnight.
     *
     * @param value task date-time to convert.
     * @return comparable local date-time.
     */
    private static LocalDateTime toLocalDateTime(TaskDateTime value) {
        return LocalDateTime.of(value.getDate(), value.getTime().orElse(java.time.LocalTime.MIDNIGHT));
    }

    /**
     * Returns a display representation that includes the event range.
     *
     * @return formatted event.
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + DateTimeParser.formatForDisplay(from) + " to: "
                + DateTimeParser.formatForDisplay(to) + ")";
    }

    /**
     * Returns the start of the event.
     *
     * @return start date-time.
     */
    public TaskDateTime getFrom() {
        return from;
    }

    /**
     * Returns the end of the event.
     *
     * @return end date-time.
     */
    public TaskDateTime getTo() {
        return to;
    }
}
