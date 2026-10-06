package moon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task with a start and end time.
 */
public class Event extends Task {
    /** The format used when showing event dates to the user. */
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    /** The date on which this event starts. */
    private final LocalDate from;

    /** The date on which this event ends. */
    private final LocalDate to;

    /**
     * Creates an event task.
     *
     * @param description the text describing the event
     * @param from the event's start date
     * @param to the event's end date
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns whether this event has the same description and dates as another event.
     *
     * @param other the task to compare with
     * @return whether both events have the same details
     */
    @Override
    public boolean hasSameDetailsAs(Task other) {
        return other instanceof Event event
                && description.equals(event.description)
                && from.equals(event.from)
                && to.equals(event.to);
    }

    /**
     * Returns this task in Moon's display format.
     *
     * @return the formatted event task
     */
    @Override
    public String toString() {
        return "[E][" + getStatusIcon() + "] " + description
                + " (from: " + from.format(DISPLAY_FORMAT)
                + " to: " + to.format(DISPLAY_FORMAT) + ")";
    }

    /**
     * Returns this event in the format used by the data file.
     *
     * @return a line that can be saved to Moon's data file
     */
    @Override
    public String toSaveFormat() {
        return "E | " + (isDone ? "1" : "0") + " | " + description
                + " | " + from + " | " + to;
    }
}
