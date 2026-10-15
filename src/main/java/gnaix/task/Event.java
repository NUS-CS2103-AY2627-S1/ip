package gnaix.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that occurs over a specified time period.
 */
public class Event extends Task {
    private static final DateTimeFormatter OUTPUT_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm", Locale.ENGLISH);
    private LocalDateTime from;
    private LocalDateTime to;

    /**
     * Creates an event with the given description and time period.
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

    public LocalDateTime getFrom() {
        return this.from;
    }

    public LocalDateTime getTo() {
        return this.to;
    }

    /**
     * Returns whether the event occurs on the specified date.
     *
     * @param date Date to check.
     * @return True if the date falls within the event period.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        LocalDate fromDate = this.from.toLocalDate();
        LocalDate toDate = this.to.toLocalDate();

        return !date.isBefore(fromDate) && !date.isAfter(toDate);
    }

    /**
     * Returns the event with its start and end times.
     *
     * @return String representation of the event.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + this.from.format(OUTPUT_DATE_TIME_FORMAT)
                + " to: " + this.to.format(OUTPUT_DATE_TIME_FORMAT) + ")";
    }
}
