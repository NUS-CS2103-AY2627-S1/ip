package sioet.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that occurs during a specified time interval.
 */
public class Event extends Task {
    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Creates an event task with its description, start time, and end time.
     *
     * @param description the event description
     * @param from        the start date and time
     * @param to          the end date and time
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        assert from != null : "Event start time should not be null";
        assert to != null : "Event end time should not be null";
        this.from = from;
        this.to = to;
    }

    @Override
    protected String getTaskType() {
        return "E";
    }

    @Override
    protected String getDetails() {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a", Locale.ENGLISH);

        return " (from: " + from.format(formatter)
                + " to: " + to.format(formatter) + ")";
    }

    /**
     * Returns the event's start date and time.
     *
     * @return the start date and time
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns the event's end date and time.
     *
     * @return the end date and time
     */
    public LocalDateTime getTo() {
        return to;
    }
}
