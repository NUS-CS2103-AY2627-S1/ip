package kiaw.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents an event occurring between a start date and an end date.
 */
public class Event extends Task {
    private LocalDate from;
    private LocalDate to;

    /**
     * Creates an event with the specified description and date range.
     *
     * @param description description of the event
     * @param from start date of the event
     * @param to end date of the event
     */
    public Event(
            String description,
            LocalDate from,
            LocalDate to
    ) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the symbol representing an event task.
     *
     * @return "E"
     */
    @Override
    public String getTypeIcon() {
        return "E";
    }

    /**
     * Returns the event details in a user-friendly format.
     *
     * @return formatted event details
     */
    @Override
    public String getDetails() {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("MMM dd yyyy");

        return description
                + " (from: "
                + from.format(formatter)
                + " to: "
                + to.format(formatter)
                + ")";
    }

    /**
     * Returns the start date of this event.
     *
     * @return event start date
     */
    public LocalDate getFrom() {
        return from;
    }

    /**
     * Returns the end date of this event.
     *
     * @return event end date
     */
    public LocalDate getTo() {
        return to;
    }

    /**
     * Returns the event start date used for chronological sorting.
     *
     * @return event start date
     */
    @Override
    public LocalDate getSortDate() {
        return from;
    }
}
