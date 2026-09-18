package aether.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

/**
 * A task that starts on one date and ends on another date.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);
    private final LocalDate from;
    private final LocalDate to;

    /**
     * Creates an event that starts as not done.
     *
     * @param description what the event is
     * @param from the date when the event starts
     * @param to the date when the event ends
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description, TaskType.EVENT);
        this.from = from;
        this.to = to;
    }

    /** Returns the date on which this event starts. */
    public LocalDate getFrom() {
        return from;
    }

    /** Returns the date on which this event ends. */
    public LocalDate getTo() {
        return to;
    }

    /**
     * Returns the event start date used to sort this task.
     *
     * @return the event start date
     */
    @Override
    public Optional<LocalDate> getSortDate() {
        return Optional.of(from);
    }

    /** Includes both event dates when comparing task details for duplicates. */
    @Override
    boolean hasSameDetails(Task other) {
        if (!super.hasSameDetails(other)) {
            return false;
        }
        Event otherEvent = (Event) other;
        return from.equals(otherEvent.from) && to.equals(otherEvent.to);
    }

    /**
     * Returns this event in list form, for example
     * {@code [E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)}.
     *
     * @return the type icon, shared task text, and start/end dates
     */
    @Override
    public String toString() {
        return "[" + getTypeIcon() + "]" + super.toString()
                + " (from: " + from.format(DISPLAY_FORMAT) + " to: " + to.format(DISPLAY_FORMAT) + ")";
    }
}
