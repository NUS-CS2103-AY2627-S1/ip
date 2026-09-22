package wiz;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

/**
 * Represents an event task occurring during a specific time period.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy HHmm", Locale.US);
    private static final DateTimeFormatter FILE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Constructs an Event task with description, start time, and end time.
     *
     * @param description The task description.
     * @param from The start date and time.
     * @param to The end date and time.
     */
    public Event(
            String description,
            LocalDateTime from,
            LocalDateTime to
    ) {
        super(description);
        assert from != null : "Event start time cannot be null";
        assert to != null : "Event end time cannot be null";
        assert !to.isBefore(from) : "Event end time cannot be before start time";
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the start date and time of the event.
     *
     * @return The start LocalDateTime.
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns the end date and time of the event.
     *
     * @return The end LocalDateTime.
     */
    public LocalDateTime getTo() {
        return to;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        Event event = (Event) obj;
        return Objects.equals(from, event.from) && Objects.equals(to, event.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), from, to);
    }

    @Override
    public String toString() {
        return "[E]"
                + super.toString()
                + " (from: "
                + from.format(DISPLAY_FORMATTER)
                + " to: "
                + to.format(DISPLAY_FORMATTER)
                + ")";
    }

    @Override
    public String toFileString() {
        return "E | "
                + (isDone ? "1" : "0")
                + " | "
                + description
                + " | "
                + from.format(FILE_FORMATTER)
                + " | "
                + to.format(FILE_FORMATTER);
    }
}