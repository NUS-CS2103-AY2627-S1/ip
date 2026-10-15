package zinc.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Represents a task with a start date-time and an end date-time. */
public class Event extends Task {
    /** The format used when displaying event date-times to the user. */
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu HH:mm", Locale.ENGLISH);
    /** The date and time at which the event starts. */
    private final LocalDateTime start;
    /** The date and time at which the event ends. */
    private final LocalDateTime end;

    /**
     * Creates an event task.
     *
     * @param description The description of the event.
     * @param start The date and time when the event starts.
     * @param end The date and time when the event ends.
     */
    public Event(String description, LocalDateTime start, LocalDateTime end) {
        super(description);
        assert start != null && end != null : "Event start and end must not be null";
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("Event end must not be before its start");
        }
        this.start = start;
        this.end = end;
    }

    /**
     * Returns the category of this task.
     *
     * @return The event task type.
     */
    @Override
    public TaskType getTaskType() {
        return TaskType.EVENT;
    }

    /**
     * Returns a display-friendly representation including the event times.
     *
     * @return The task name, completion status, and event interval.
     */
    @Override
    public String toString() {
        return super.toString() + " (from: "
                + start.format(DISPLAY_DATE_TIME_FORMAT).toUpperCase(Locale.ENGLISH)
                + " to: " + end.format(DISPLAY_DATE_TIME_FORMAT).toUpperCase(Locale.ENGLISH) + ")";
    }

    /** Returns the date and time at which the event starts. */
    public LocalDateTime getStart() {
        return start;
    }

    /** Returns the date and time at which the event ends. */
    public LocalDateTime getEnd() {
        return end;
    }
}
