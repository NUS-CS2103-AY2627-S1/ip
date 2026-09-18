package waffles;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

/**
 * Represents a task that occurs between a start and end date or time.
 */
public class Event extends Task {
    /** The date when the event starts. */
    protected LocalDate from;

    /** The date when the event ends. */
    protected LocalDate to;

    /**
     * Creates a new event task.
     *
     * @param description the text describing the event
     * @param from the event's start date
     * @param to the event's end date
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("An event must end on or after it starts.");
        }
    }

    /**
     * Creates an event from ISO-8601 dates such as {@code 2019-10-15}.
     *
     * @param description the text describing the event
     * @param from the ISO-8601 start date
     * @param to the ISO-8601 end date
     */
    public Event(String description, String from, String to) {
        this(description, LocalDate.parse(from), LocalDate.parse(to));
    }

    /**
     * Returns the event's starting date.
     *
     * @return the event start value
     */
    public LocalDate getFrom() {
        return from;
    }

    /**
     * Returns the event's ending date.
     *
     * @return the event end value
     */
    public LocalDate getTo() {
        return to;
    }

    /**
     * Returns the event in the format used by the task list.
     *
     * @return the event type marker, status, description, and time range
     */
    @Override
    public String toString() {
        DateTimeFormatter displayFormat = DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
        return "[E]" + super.toString() + " (from: " + from.format(displayFormat)
                + " | to: " + to.format(displayFormat) + ")";
    }
}
