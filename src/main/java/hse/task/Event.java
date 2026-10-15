package hse.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Represents a task that occurs during a specified time range. */
public class Event extends Task {
    public static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);
    public static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu h:mm a", Locale.ENGLISH);
    private LocalDateTime from;
    private LocalDateTime to;

    /**
     * Creates an event with the supplied description, start, and end date-times.
     *
     * @throws IllegalArgumentException if the end is not after the start.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        if (!from.isBefore(to)) {
            throw new IllegalArgumentException("An event cannot end before it starts.");
        }
        this.from = from;
        this.to = to;
    }

    /** Returns whether this event occurs on any part of the supplied date. */
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(from.toLocalDate()) && !date.isAfter(to.toLocalDate());
    }

    @Override
    LocalDateTime getSortDateTime() {
        return from;
    }

    /** Returns this event in the standard event display format. */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from.format(DATE_TIME_FORMAT)
                + " to: " + to.format(DATE_TIME_FORMAT) + ")";
    }
}
