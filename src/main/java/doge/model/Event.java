package doge.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Represents a task that occurs during a specified time interval. */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a", Locale.ENGLISH);
    private final LocalDateTime from;
    private final LocalDateTime to;

    /** Creates an incomplete event task with a start and end time. */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        if (from == null || to == null) {
            throw new IllegalArgumentException("An event must have a start and end date and time");
        }
        if (!to.isAfter(from)) {
            throw new IllegalArgumentException(
                    "An event's end date and time must be later than its start date and time");
        }
        this.from = from;
        this.to = to;
    }

    /** Returns the event's start date and time. */
    public LocalDateTime getFrom() {
        return from;
    }

    /** Returns the event's end date and time. */
    public LocalDateTime getTo() {
        return to;
    }

    /** Returns this event in the pipe-delimited persistence format. */
    @Override
    public String toStorageString() {
        String status = isDone ? "1" : "0";
        String taskData = "E | " + status + " | " + description + " | " + from + " | " + to;
        return getPriority() == 0 ? taskData : taskData + " | " + getPriority();
    }

    /** Returns this event in the format displayed by the user interface. */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + from.format(DISPLAY_FORMATTER)
                + " to: " + to.format(DISPLAY_FORMATTER) + ")";
    }
}
