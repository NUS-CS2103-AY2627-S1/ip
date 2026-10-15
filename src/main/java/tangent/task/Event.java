package tangent.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Represents a task with a description, a start time and an end time. */
public class Event extends Task {
    /** Specifies the format of the date output to be displayed by {@link Event#toString()}. */
    public static final DateTimeFormatter OUTPUT_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma", Locale.ENGLISH);
    /** The start time of the task. */
    private final LocalDateTime from;
    /** The end time of the task. */
    private final LocalDateTime to;

    /** Creates a new Event object with a description, a start time and an end time. */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /** Returns the event start date and time. */
    public LocalDateTime getFrom() {
        return from;
    }

    /** Returns the event end date and time. */
    public LocalDateTime getTo() {
        return to;
    }

    /** Converts the Event object to a String to be displayed to the user. */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from.format(OUTPUT_FORMATTER)
                + " to: " + to.format(OUTPUT_FORMATTER) + ")";
    }
}
