package ducky;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Represents a task with a start and end time/date.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy h:mm a", Locale.ENGLISH);

    private final LocalDateTime start;
    private final LocalDateTime end;

    /**
     * Creates an event task with the given description and time range.
     *
     * @param description the description of the task.
     * @param start the event start time.
     * @param end the event end time.
     */
    public Event(String description, LocalDateTime start, LocalDateTime end) {
        this(description, start, end, List.of());
    }

    /**
     * Creates an event task with the given description, time range, and tags.
     *
     * @param description the description of the task.
     * @param start the event start time.
     * @param end the event end time.
     * @param tags the tags associated with the task.
     */
    public Event(String description, LocalDateTime start, LocalDateTime end, List<String> tags) {
        super(description, tags);
        assert start != null : "An event must have a start time";
        assert end != null : "An event must have an end time";
        assert !end.isBefore(start) : "An event cannot end before it starts";
        this.start = start;
        this.end = end;
    }

    /**
     * Returns the event start time.
     *
     * @return the event start time.
     */
    public LocalDateTime getStart() {
        return start;
    }

    /**
     * Returns the event end time.
     *
     * @return the event end time.
     */
    public LocalDateTime getEnd() {
        return end;
    }

    /**
     * Returns this event in Ducky's display format.
     *
     * @return the formatted event.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + String.format(" (from: %s to: %s)",
                start.format(DISPLAY_FORMAT), end.format(DISPLAY_FORMAT));
    }
}
