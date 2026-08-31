package bibi.task;

import java.time.LocalDate;

import bibi.BibiException;

/**
 * Represents a task that runs from a start date to an end date, each optionally
 * carrying a time of day.
 */
public class Event extends Task {
    /** Shown when any of the three pieces of event text is missing. */
    private static final String MISSING_FIELD_MESSAGE =
            "An event needs a description, /from time, and /to time.";

    private final TaskDateTime from;
    private final TaskDateTime to;

    /**
     * Creates an event with a description, starting time, and ending time.
     *
     * @param description text describing the event
     * @param from the event start date, in one of the accepted date formats
     * @param to the event end date, in one of the accepted date formats
     * @throws BibiException if a field is blank, a date cannot be read, or the
     *     event would end before it starts
     */
    public Event(String description, String from, String to) throws BibiException {
        super(requireTaskText(description, MISSING_FIELD_MESSAGE));
        this.from = TaskDateTime.parse(requireTaskText(from, MISSING_FIELD_MESSAGE));
        this.to = TaskDateTime.parse(requireTaskText(to, MISSING_FIELD_MESSAGE));

        // Storing real dates rather than text makes this check possible at last.
        if (this.to.isBefore(this.from)) {
            throw new BibiException("An event cannot end before it starts.");
        }
    }

    @Override
    protected String getTypeCode() {
        return "E";
    }

    @Override
    protected String getDetails() {
        return " (from: " + from + " to: " + to + ")";
    }

    /**
     * Returns the start and end dates as the two extra save-file fields.
     */
    @Override
    protected String getSaveFields() {
        return " " + FIELD_SEPARATOR + " " + from.toStorageString()
                + " " + FIELD_SEPARATOR + " " + to.toStorageString();
    }

    /**
     * Reports whether this event is running on the supplied date, counting the
     * first and last days of a multi-day event as part of it.
     */
    @Override
    public boolean occursOn(LocalDate queryDate) {
        return !queryDate.isBefore(from.getDate()) && !queryDate.isAfter(to.getDate());
    }
}
