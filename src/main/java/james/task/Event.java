package james.task;

import java.time.LocalDate;

/**
 * Represents an event task occurring during a specific time period.
 */
public class Event extends Task {
    private static final String EVENT_STORAGE_PREFIX = "E | ";
    private static final String STORAGE_FIELD_SEPARATOR = " | ";
    private static final String EVENT_DISPLAY_PREFIX = "[E]";
    private static final String EVENT_DATES_FORMAT = " (from: %s to: %s)";

    private final LocalDate from;
    private final LocalDate to;

    /**
     * Initializes an uncompleted event task with the given description, start date, and end date.
     *
     * @param description Description of the event.
     * @param from Start date of the event.
     * @param to End date of the event.
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public Task copy() {
        Task copy = new Event(description, from, to);
        copy.isDone = isDone;
        return copy;
    }

    /**
     * Returns the start date of the event.
     *
     * @return Start date.
     */
    public LocalDate getFrom() {
        return from;
    }

    /**
     * Returns the end date of the event.
     *
     * @return End date.
     */
    public LocalDate getTo() {
        return to;
    }

    /**
     * Returns the string representation of the event task formatted for file storage.
     *
     * @return Formatted event string for storage.
     */
    @Override
    public String toFileString() {
        return EVENT_STORAGE_PREFIX + super.toFileString() + STORAGE_FIELD_SEPARATOR + from +
                STORAGE_FIELD_SEPARATOR + to;
    }

    /**
     * Returns the string representation of the event task for display.
     *
     * @return Formatted display string of the event task.
     */
    @Override
    public String toString() {
        return EVENT_DISPLAY_PREFIX + super.toString() + EVENT_DATES_FORMAT.formatted(
                from.format(DISPLAY_FORMAT),
                to.format(DISPLAY_FORMAT));
    }
}
