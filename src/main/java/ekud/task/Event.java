package ekud.task;

import java.time.LocalDate;

/**
 * A task that starts and ends at given dates or date-times.
 */
public class Event extends Task {
    private final TaskDateTime from;
    private final TaskDateTime to;

    /**
     * Creates an event with the given description and start/end date-times.
     *
     * @param description text describing the event
     * @param from        start date or date-time
     * @param to          end date or date-time
     */
    public Event(String description, TaskDateTime from, TaskDateTime to) {
        super(description);
        // Parser validates both flags before constructing; a missing date is a programming error.
        assert from != null : "Event start date/time must be provided";
        assert to != null : "Event end date/time must be provided";
        // Parser also rejects an inverted range; this documents that invariant at construction.
        assert !to.toLocalDate().isBefore(from.toLocalDate())
                : "Event end date should not be before the start date";
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the start date or date-time.
     *
     * @return the {@code /from} value
     */
    public TaskDateTime getFrom() {
        return from;
    }

    /**
     * Returns the end date or date-time.
     *
     * @return the {@code /to} value
     */
    public TaskDateTime getTo() {
        return to;
    }

    /**
     * Returns {@code E}, the type icon for events.
     *
     * @return {@code E}
     */
    @Override
    public String getTypeIcon() {
        return "E";
    }

    /**
     * Returns whether {@code date} falls in this event's start–end range, inclusive.
     *
     * @param date the calendar date to test
     * @return {@code true} if the date is on or between the start and end dates
     */
    @Override
    public boolean occursOn(LocalDate date) {
        LocalDate start = from.toLocalDate();
        LocalDate end = to.toLocalDate();
        return !date.isBefore(start) && !date.isAfter(end);
    }

    /**
     * Returns the start and end date/times in {@code (from: ... to: ...)} form.
     *
     * @return the event suffix
     */
    @Override
    protected String getExtraDetails() {
        return " (from: " + from.toDisplayString() + " to: " + to.toDisplayString() + ")";
    }

    /**
     * Returns {@code event DESCRIPTION /from START /to END} using the canonical date form.
     *
     * @return the add-command text for this event
     */
    @Override
    public String toCommandString() {
        return "event " + getDescription()
                + " /from " + from.toSaveString() + " /to " + to.toSaveString();
    }
}
