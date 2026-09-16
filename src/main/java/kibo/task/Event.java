package kibo.task;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

/**
 * Represents a task that occurs between a start and end date or time.
 */
public class Event extends Task {
    /** User-entered start date or time. */
    private final String from;
    /** User-entered end date or time. */
    private final String to;
    /** Calendar date parsed from the event start, when one was supplied. */
    private final Optional<LocalDate> scheduledDate;

    /**
     * Creates an event task with the given description, start, and end.
     *
     * @param description description of the event.
     * @param from start date or time stored as entered by the user.
     * @param to end date or time stored as entered by the user.
     */
    public Event(String description, String from, String to) {
        super(description);
        assert from != null && !from.isBlank() : "An event must have a start";
        assert to != null && !to.isBlank() : "An event must have an end";

        this.from = from;
        this.to = to;
        this.scheduledDate = parseScheduledDate(from);
    }

    /**
     * Returns the event start text for storage.
     *
     * @return event start text.
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the event end text for storage.
     *
     * @return event end text.
     */
    public String getTo() {
        return to;
    }

    /**
     * Returns whether this event starts on the supplied date.
     *
     * @param date date whose schedule is being viewed.
     * @return {@code true} when the event has a parsed start date equal to the supplied date.
     */
    @Override
    public boolean isScheduledOn(LocalDate date) {
        assert date != null : "A schedule date must not be null";
        return scheduledDate.map(date::equals).orElse(false);
    }

    /**
     * Parses an ISO date when the event start begins with one.
     *
     * @param from event start text.
     * @return parsed date, or an empty value for a legacy free-form event start.
     */
    private static Optional<LocalDate> parseScheduledDate(String from) {
        String dateText = from.split("\\s+", 2)[0];
        try {
            return Optional.of(LocalDate.parse(dateText));
        } catch (DateTimeParseException exception) {
            return Optional.empty();
        }
    }

    /**
     * Returns this task in the event display format.
     *
     * @return task text containing the {@code [E]} type marker, start, and end.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
