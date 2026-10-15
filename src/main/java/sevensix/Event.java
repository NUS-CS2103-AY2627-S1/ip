package sevensix;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Represents a task that starts and ends at specified dates or times.
 */
public class Event extends Task {
    /** The calendar date on which the event starts. */
    private final LocalDate from;
    /** The optional clock time at which the event starts. */
    private final LocalTime fromTime;
    /** The calendar date on which the event ends. */
    private final LocalDate to;
    /** The optional clock time at which the event ends. */
    private final LocalTime toTime;

    /**
     * Creates an event task that is initially not done.
     *
     * @param description the text describing the task.
     * @param from the start date.
     * @param to the end date.
     */
    public Event(String description, LocalDate from, LocalDate to) {
        this(description, from, null, to, null);
    }

    /**
     * Creates an event task with a start and end date-time.
     *
     * @param description the text describing the task.
     * @param from the start date-time.
     * @param to the end date-time.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        this(description, from.toLocalDate(), from.toLocalTime(), to.toLocalDate(), to.toLocalTime());
    }

    /**
     * Creates an event task with dates and optional times.
     *
     * @param description the text describing the task.
     * @param from the start date.
     * @param fromTime the optional start time.
     * @param to the end date.
     * @param toTime the optional end time.
     */
    public Event(String description, LocalDate from, LocalTime fromTime, LocalDate to,
            LocalTime toTime) {
        super(description, TaskType.EVENT);
        assert from != null && to != null
                : "An event always has start and end dates; only their times are optional";
        this.from = from;
        this.fromTime = fromTime;
        this.to = to;
        this.toTime = toTime;
    }

    /**
     * Returns the event start details for persistence.
     *
     * @return the start date.
     */
    public LocalDate getFrom() {
        return from;
    }

    /**
     * Returns the event start time for persistence.
     *
     * @return the start time, or {@code null} when only a date was supplied.
     */
    public LocalTime getFromTime() {
        return fromTime;
    }

    /**
     * Returns the event end details for persistence.
     *
     * @return the end date.
     */
    public LocalDate getTo() {
        return to;
    }

    /**
     * Returns the event end time for persistence.
     *
     * @return the end time, or {@code null} when only a date was supplied.
     */
    public LocalTime getToTime() {
        return toTime;
    }

    /**
     * Returns the event task with its start and end dates or times.
     *
     * @return the formatted event task.
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + DateTimeParser.formatForDisplay(from, fromTime)
                + " to: " + DateTimeParser.formatForDisplay(to, toTime) + ")";
    }
}
