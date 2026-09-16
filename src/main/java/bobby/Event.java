package bobby;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/** Represents a task with a start and end date. */
public class Event extends Task {
    /** Strict parser for event date input. */
    private static final DateTimeFormatter DATE_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd")
                    .withResolverStyle(ResolverStyle.STRICT);
    /** Formatter for displaying event dates. */
    private static final DateTimeFormatter DATE_DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    /** Date on which the event starts. */
    private final LocalDate from;
    /** Date on which the event ends. */
    private final LocalDate to;

    /**
     * Creates an incomplete event task.
     *
     * @param description text describing the event
     * @param from date on which the event starts
     * @param to date on which the event ends
     * @throws IllegalArgumentException if the start date is after the end date
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("An event cannot end before it starts.");
        }
        this.from = from;
        this.to = to;
    }

    /**
     * Creates an event by parsing two ISO dates.
     *
     * @param description text describing the event
     * @param fromInput start date in {@code yyyy-MM-dd} format
     * @param toInput end date in {@code yyyy-MM-dd} format
     * @return an event containing the parsed start and end dates
     * @throws DateTimeParseException if either input is not a valid ISO date
     * @throws IllegalArgumentException if the start date is after the end date
     */
    public static Event fromInput(String description, String fromInput, String toInput)
            throws DateTimeParseException {
        LocalDate from = LocalDate.parse(fromInput, DATE_INPUT_FORMAT);
        LocalDate to = LocalDate.parse(toInput, DATE_INPUT_FORMAT);
        return new Event(description, from, to);
    }

    /** Returns the date on which this event starts. */
    public LocalDate getFrom() {
        return from;
    }

    /** Returns the date on which this event ends. */
    public LocalDate getTo() {
        return to;
    }

    /** Returns whether another task has the same type, description, and event dates. */
    @Override
    public boolean hasSameDetails(Task other) {
        if (!(other instanceof Event otherEvent)) {
            return false;
        }
        return super.hasSameDetails(other)
                && from.equals(otherEvent.from)
                && to.equals(otherEvent.to);
    }

    /**
     * Returns this event task in the format used by the command line interface.
     *
     * @return the event's display text
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + getDisplayDate(from)
                + " to: " + getDisplayDate(to) + ")";
    }

    /**
     * Returns an event date in the human-friendly display format.
     *
     * @param date date to format
     * @return formatted date
     */
    private String getDisplayDate(LocalDate date) {
        return date.format(DATE_DISPLAY_FORMAT);
    }

    /**
     * Returns this event in the format used when saving tasks to disk.
     *
     * @return the event's storage line
     */
    @Override
    public String toStorageString() {
        return "E | " + getStorageStatus() + " | " + getDescription() + " | "
                + from.format(DATE_INPUT_FORMAT) + " | " + to.format(DATE_INPUT_FORMAT)
                + " | " + getPriority();
    }
}
