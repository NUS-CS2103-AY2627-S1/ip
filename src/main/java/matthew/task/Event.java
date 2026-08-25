package matthew.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Represents a task that occurs over a specified date/time range. */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a");

    /** Start date and time of the event. */
    protected LocalDateTime from;
    /** End date and time of the event. */
    protected LocalDateTime to;

    /**
     * Creates an incomplete event with the specified description and time range.
     *
     * @param description Description of the event.
     * @param from Start date and time of the event.
     * @param to End date and time of the event.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the start date and time of the event.
     *
     * @return Event start date and time.
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns the end date and time of the event.
     *
     * @return Event end date and time.
     */
    public LocalDateTime getTo() {
        return to;
    }

    /**
     * Returns whether this event overlaps the specified calendar date.
     *
     * @param date Date to check.
     * @return {@code true} if the event overlaps the date.
     */
    public boolean occursOn(LocalDate date) {
        LocalDate startDate = from.toLocalDate();
        LocalDate endDate = to.toLocalDate();

        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /** Returns the event formatted with its time range and completion status. */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + from.format(DISPLAY_FORMAT)
                + " to: " + to.format(DISPLAY_FORMAT) + ")";
    }
}
